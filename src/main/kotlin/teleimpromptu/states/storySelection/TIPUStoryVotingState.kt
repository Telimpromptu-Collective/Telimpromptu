package teleimpromptu.states.storySelection

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.*
import teleimpromptu.message.*
import teleimpromptu.states.promptAnswering.TIPUPromptAnsweringState

class TIPUStoryVotingState(private val players: List<TIPUStorySelectionPlayer>,
                           private val tipuSession: TIPUSession): TIPUSessionState {

    // vote option id is equal to the index in this list
    private val storyOptions: MutableMap<Int, TIPUStory> = getDefaultStoryOptions()
        .withIndex().associate { it.index to it.value }.toMutableMap()
    private val storyVotes: MutableMap<TIPUStorySelectionPlayer, Int> = mutableMapOf()

    init {
        players.forEach {
            it.connection.send(jsonDecoder
                .encodeToString(EnterStoryVotingStateMessage()))
        }
    }

    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        // find the sender player, return if its not someone we know
        val sender = players.firstOrNull { it.connection == ctx } ?: return

        when (message) {
            is StorySubmissionMessage -> {
                val usersAlreadySubmittedStoryOption = storyOptions.entries.find { it.value.author == sender }

                if (usersAlreadySubmittedStoryOption != null) {
                    val usersStory = usersAlreadySubmittedStoryOption.value
                    val usersIndex = usersAlreadySubmittedStoryOption.key

                    // update that user's story
                    storyOptions[usersIndex] = TIPUStory(sender, message.story)

                    // wipe all the votes

                    // all the users that voted for this
                    val usersVotedForOldStory = storyVotes.entries.filter { it.value == usersIndex }.map { it.key }
                    // remove their votes
                    usersVotedForOldStory.forEach { storyVotes.remove(it) }
                } else {
                    // create a new story... we dont remove stories ever so this should hold
                    storyOptions[storyOptions.size] = TIPUStory(sender, message.story)
                }

                // update the users
                players.forEach { it.connection.send(jsonDecoder
                    .encodeToString(buildStoryVotingStateUpdateMessage())) }
            }
            is StoryVoteMessage -> {
                if (!storyOptions.contains(message.storyId)) {
                    println("what the heck wrong vote id")
                    return
                }

                storyVotes[sender] = message.storyId

                // update the users
                players.forEach { it.connection.send(jsonDecoder
                    .encodeToString(buildStoryVotingStateUpdateMessage())) }
            }
            is EndStoryVotingMessage -> {
                if (players.all { storyVotes.keys.contains(it) }) {
                    // shuffled so its random on a tie
                    val winningStoryIndex = storyVotes.values.shuffled()
                        .groupingBy { it }.eachCount().maxBy { it.value }.key
                    val winningStory = storyOptions[winningStoryIndex] ?: error("story was not found in voting")

                    tipuSession.state = TIPUPromptAnsweringState(players, winningStory.story, tipuSession)
                }
            }
            else -> println("fail game: $message")
        }
    }

    override fun recieveConnectionMessage(ctx: WsMessageContext, message: UserConnectMessage) {
        // if this user is connecting with a username not in the lobby, stop
        val reconnectingUser = players.find { it.username == message.username } ?: return

        // if someone already connected with this username kick them and set this as the new one lol
        reconnectingUser.connection.closeSession()
        reconnectingUser.connection = ctx

        // send the state...
        ctx.send(jsonDecoder
            .encodeToString(EnterStoryVotingStateMessage()))
        ctx.send(buildStoryVotingStateUpdateMessage())
    }

    private fun buildStoryVotingStateUpdateMessage(): StoryVotingStateUpdateMessage {
        return StoryVotingStateUpdateMessage(
            storyOptions.entries.map { storyOption ->
                StoryForClient(
                    storyOption.key,
                    storyOption.value.author?.username,
                    storyOption.value.story,
                    storyVotes.filter { storyVote -> storyVote.value == storyOption.key }
                        .map { storyVote -> storyVote.key.username }
                )
            }
        )

    }

    // todo this will trigger when we recieve any disconnect
    override fun receiveDisconnect(ctx: WsCloseContext) {
        println("connection closed....")

        val updateMessage = UsernameUpdateMessage(
            players.map { player ->
                UsernameStatus(player.username, player.connection.session.isOpen)
            }
        )

        val json = jsonDecoder.encodeToString(updateMessage)

        players.forEach { player ->
            player.connection.send(json)
        }
    }

    private fun getDefaultStoryOptions(): List<TIPUStory> {
        return listOf()
    }
}