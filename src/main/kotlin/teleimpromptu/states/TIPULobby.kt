package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import java.util.concurrent.ConcurrentHashMap

class TIPULobby(private val tipuSession: TIPUSession) : TIPUSessionState {
    private val usernameMap = ConcurrentHashMap<String, WsContext>()

    val players: List<Pair<String, WsContext>>
        get() {
            return usernameMap.entries.map { Pair(it.key, it.value) }
        }

    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is StartGameMessage -> {
                // tipuSession.setState()
            }
            is CreateUserMessage -> {
                // if a player connects with a preexisting session, remove their old one.
                usernameMap.filter { entry ->
                    entry.value.session == ctx.session
                }.forEach { entry ->
                    usernameMap.remove(entry.key)
                }

                // if someone already connected with this username kick them lol
                usernameMap[message.username]?.session?.close()

                usernameMap[message.username] = ctx
                ctx.send(jsonDecoder.encodeToString(ConnectionSuccessMessage()))

                updateUserStatuses()
            }
            else -> println("fail")
        }
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {
        usernameMap.filter { entry ->
            entry.value.session == ctx.session
        }.forEach { entry ->
            usernameMap.remove(entry.key)
        }

        updateUserStatuses()
    }

    private fun updateUserStatuses() {
        val updateMessage = UsernameUpdateMessage(
            usernameMap.map { entry ->
                UsernameStatus(entry.key, entry.value.session.isOpen)
            }
        )

        val json = jsonDecoder.encodeToString(updateMessage)

        usernameMap.values.forEach { ws ->
            ws.send(json)
        }
    }
}