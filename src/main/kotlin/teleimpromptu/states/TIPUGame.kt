package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import teleimpromptu.script.allocating.AdlibPrompt
import teleimpromptu.script.allocating.DetailedPrompt
import teleimpromptu.script.allocating.Prompt
import teleimpromptu.script.allocating.PromptAllocator
import teleimpromptu.script.parsing.PromptGroup
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SinglePrompt

class TIPUGame(private val players: List<TIPUPlayer>,
               private val script: List<ScriptSection>,
               private val tipuSession: TIPUSession): TIPUSessionState {

    private val promptAllocator: PromptAllocator = PromptAllocator(players, script)

    // promptids to promptanswers
    val promptAnswers: MutableMap<String, String> = mutableMapOf()

    init {
        sendPromptsToUsers(promptAllocator.allocateAvailablePrompts(listOf()))
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is PromptResponseMessage -> {
                sendPromptsToUsers(promptAllocator.allocateAvailablePrompts(listOf(message.id)))
            }
            else -> println("fail")
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

            val adlibPrompts = entry.value.filterIsInstance<AdlibPrompt>()



            val json = jsonDecoder.encodeToString(NewPromptsMessage(unpackedScriptPrompts, adlibPrompts))

            entry.key.connection.send(json)
        }
    }
}