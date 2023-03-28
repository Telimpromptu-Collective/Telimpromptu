package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.ConnectionSuccessMessage
import teleimpromptu.message.CreateUserMessage
import teleimpromptu.message.Message
import teleimpromptu.message.StartGameMessage
import teleimpromptu.script.parsing.ScriptSection

class TIPUGame(private val players: List<TIPUPlayer>,
               private val script: List<ScriptSection>,
               private val tipuSession: TIPUSession): TIPUSessionState {

    init {

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