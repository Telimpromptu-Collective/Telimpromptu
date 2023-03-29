package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import teleimpromptu.script.allocating.DetailedPrompt
import teleimpromptu.script.allocating.PromptAllocator
import teleimpromptu.script.parsing.ScriptSection

class TIPUGame(private val players: List<TIPUPlayer>,
               private val script: List<ScriptSection>,
               private val tipuSession: TIPUSession): TIPUSessionState {

    val promptAllocator: PromptAllocator = PromptAllocator(players, script)

    // promptids to promptanswers
    val promptAnswers: MutableMap<String, String> = mutableMapOf()

    init {
        val json = jsonDecoder.encodeToString(NewPromptsMessage(script.flatMap { it.prompts }))

        players.forEach { player ->
            player.connection.send(json)
        }
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is PromptResponseMessage -> {
                // this is rife for hackage...
                message.response
            }
            else -> println("fail")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {

    }
}