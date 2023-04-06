package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.*
import teleimpromptu.message.*
import teleimpromptu.script.allocating.AdlibPrompt
import teleimpromptu.script.allocating.Prompt
import teleimpromptu.script.allocating.PromptAllocator
import teleimpromptu.script.formatting.PromptFormatter
import teleimpromptu.script.parsing.PromptGroup
import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SinglePrompt

class TIPUGame(private val players: List<TIPUPlayer>,
               private val script: List<ScriptSection>,
               private val tipuSession: TIPUSession): TIPUSessionState {

    private val promptAllocator: PromptAllocator = PromptAllocator(players, script)
    private val promptFormatter: PromptFormatter = PromptFormatter(players)

    init {
        // send everyone their prompts. empty list because there are no newly completed prompts
        promptAllocator.allocateAvailablePrompts().forEach { sendPromptsToPlayer(it.key, it.value) }
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        // find the sender player, return if its not someone we know
        val sender = players.firstOrNull { it.connection == ctx } ?: return

        when (message) {
            is PromptResponseMessage -> {
                println("recieved: " + message.id + ": " + message.response)

                // todo trusting this id allows hackerz!
                promptFormatter.addPromptResponse(message.id, message.response)

                // get the prompts and send the prompts to everyone
                promptAllocator.moveCompletedPromptsAndAllocate(listOf(message.id), sender)
                    .forEach { sendPromptsToPlayer(it.key, it.value) }

                if (promptAllocator.areAllPromptsComplete()) {
                    players.forEach { it.connection.send(jsonDecoder.encodeToString(PromptsCompleteMessage())) }
                }
            }
            // user reconnect
            is CreateUserMessage -> {
                // if this user is connecting with a username not in the lobby, stop
                val reconnectingUser = players.find { it.username == message.username } ?: return

                // if someone already connected with this username kick them and set this as the new one lol
                reconnectingUser.connection.closeSession()
                reconnectingUser.connection = ctx

                // send them the start game info to get them up to speed
                val json = jsonDecoder
                    .encodeToString(GameStartedMessage(players.map { IngamePlayerStatus(it.username, it.role.toLowercaseString()) }))
                reconnectingUser.connection.send(json)

                // send them their incomplete prompts
                sendPromptsToPlayer(reconnectingUser, promptAllocator.outstandingPromptsForPlayer(reconnectingUser))
            }
            else -> println("fail game: $message")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {
        println("connection closed....")
    }

    // todo maybe this should be unpacked beforehand since reconnecting has to be
    private fun sendPromptsToPlayer(player: TIPUPlayer, prompts: List<SinglePrompt>) {
        // format script prompts
        val formattedScriptPrompts = prompts.map { SinglePrompt(it.id, promptFormatter.formatText(it.description)) }

        player.connection.send(jsonDecoder.encodeToString(NewPromptsMessage(formattedScriptPrompts)))
    }

    fun getFullFormattedScript(): List<ScriptLine> {
        return script.flatMap { it.lines }.map { ScriptLine(it.speaker, promptFormatter.formatText(it.text)) }
    }

    fun getPlayers(): List<TIPUPlayer> {
        return players
    }
}