package teleimpromptu.states.storySelection

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.*
import teleimpromptu.message.*

class TIPUStoryVotingState(private val players: List<TIPUStorySelectionPlayer>,
                           userStories: List<TIPUStory>,
                           private val tipuSession: TIPUSession): TIPUSessionState {

    private val playerToVoteMap: MutableMap<TIPUStorySelectionPlayer, Int> = mutableMapOf()

    private val storyOptions: List<TIPUStory> = userStories + getDefaultStoryOptions()

    init {
        val json = jsonDecoder
            .encodeToString(
                EnterStoryVotingStateMessage(storyOptions.map {
                    val authorName = it.author?.username ?: "the powers that be"
                    StoryForClient(authorName, it.story)
                }
                )
            )

        players.forEach {
            it.connection.send(json)
        }
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        // find the sender player, return if its not someone we know
        val sender = players.firstOrNull { it.connection == ctx } ?: return

        when (message) {
            is StoryVoteMessage -> {
                playerToVoteMap[sender] = message.storyId

                // tell the players what everyone has voted for
                players.forEach {
                    it.connection.send(StoryVotesUpdate(playerToVoteMap.entries.associate { entry ->
                        entry.key.username to entry.value
                    }))
                }

                if (playerToVoteMap.keys == players) {

                }
            }
            // user reconnect
            is UserConnectMessage -> {
                // if this user is connecting with a username not in the lobby, stop
                val reconnectingUser = players.find { it.username == message.username } ?: return

                // if someone already connected with this username kick them and set this as the new one lol
                reconnectingUser.connection.closeSession()
                reconnectingUser.connection = ctx

                // send the state...
            }
            else -> println("fail game: $message")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {
        println("connection closed....")
    }

    private fun getDefaultStoryOptions(): List<TIPUStory> {
        return listOf()
    }
}