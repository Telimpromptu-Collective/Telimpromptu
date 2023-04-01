package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
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
        sendPromptsToUsers(promptAllocator.allocateAvailablePrompts(listOf()))
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is PromptResponseMessage -> {
                println("recieved: " + message.id + ": " + message.response)

                // todo trusting this id allows hackerz!
                promptFormatter.addPromptResponse(message.id, message.response)
                sendPromptsToUsers(promptAllocator.allocateAvailablePrompts(listOf(message.id)))
            }
            else -> println("fail game: $message")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {

    }

    private fun sendPromptsToUsers(usersToPrompts: Map<TIPUPlayer, List<Prompt>>) {
        for (entry in usersToPrompts) {
            // unpack groups into a list containing all scriptPrompts
            val unpackedScriptPrompts = entry.value.flatMap {
                    when (it) {
                        is SinglePrompt -> {
                            listOf(it)
                        }
                        is PromptGroup -> {
                            it.subPrompts
                        }
                        // essentially filter out adlibs
                        else -> {
                            listOf()
                        }
                    }
                }

            // format script prompts
            val formattedScriptPrompts = unpackedScriptPrompts
                .map { SinglePrompt(it.id, promptFormatter.formatText(it.description)) }

            val adlibPrompts = entry.value.filterIsInstance<AdlibPrompt>()

            val json = jsonDecoder.encodeToString(NewPromptsMessage(formattedScriptPrompts, adlibPrompts))

            entry.key.connection.send(json)
        }
    }

    fun getFullFormattedScript(): List<ScriptLine> {
        return script.flatMap { it.lines }.map { ScriptLine(it.speaker, promptFormatter.formatText(it.text)) }
    }

    fun getPlayers(): List<TIPUPlayer> {
        return players
    }
}