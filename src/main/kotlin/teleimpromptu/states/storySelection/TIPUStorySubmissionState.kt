package teleimpromptu.states.storySelection

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.*
import teleimpromptu.message.*

class TIPUStorySubmissionState(private val players: List<TIPUStorySelectionPlayer>,
                               private val tipuSession: TIPUSession): TIPUSessionState {

    private val submittedStories: MutableMap<TIPUStorySelectionPlayer, String> = mutableMapOf()
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        // find the sender player, return if its not someone we know
        val sender = players.firstOrNull { it.connection == ctx } ?: return

        when (message) {
            is StorySubmissionMessage -> {
                submittedStories[sender] = message.story

                if (submittedStories.keys == players) {
                    tipuSession.state = TIPUStoryVotingState(players,
                        submittedStories.map { TIPUStory(it.key, it.value) },
                        tipuSession)
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
}