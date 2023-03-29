package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import teleimpromptu.script.allocating.DetailedScriptPrompt
import teleimpromptu.script.parsing.ScriptSection

class TIPUGame(private val players: List<TIPUPlayer>,
               private val script: List<ScriptSection>,
               private val tipuSession: TIPUSession): TIPUSessionState {

    val remainingPrompts: MutableList<DetailedScriptPrompt> = mutableListOf()

    val promptAnswers: MutableMap<DetailedScriptPrompt, String> = mutableMapOf()

    init {
        val json = jsonDecoder.encodeToString(NewPromptsMessage(script.flatMap { it.prompts }))

        players.forEach { player ->
            player.connection.send(json)
        }
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is PromptResponseMessage -> {
                message.response
            }
            else -> println("fail")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {

    }
}