package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import teleimpromptu.script.parsing.ScriptSection

class TIPUGame(private val players: List<TIPUPlayer>,
               private val script: List<ScriptSection>,
               private val tipuSession: TIPUSession): TIPUSessionState {

    init {
        val json = jsonDecoder.encodeToString(NewPromptsMessage(script.flatMap { it.prompts }))

        players.forEach { player ->
            player.connection.send(json)
        }
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is StartGameMessage -> {
                // tipuSession.setState()
            }
            is CreateUserMessage -> {
                /*
                // if a player connects with a preexisting session, remove their old one.
                usernameMap.filter { entry ->
                    entry.value.session == ctx.session
                }.forEach { entry ->
                    usernameMap.remove(entry.key)
                }

                // if someone already connected with this username kick them lol
                usernameMap[message.username]?.session?.close()

                usernameMap[message.username] = ctx
                ctx.send(Klaxon().toJsonString(ConnectionSuccessMessage()))

                updateUserStatuses()

                 */
            }
            else -> println("fail")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {

    }
}