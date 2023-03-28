package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import teleimpromptu.script.building.ScriptBuilderService
import java.util.concurrent.ConcurrentHashMap

class TIPULobby(private val tipuSession: TIPUSession) : TIPUSessionState {
    private val usernameMap = ConcurrentHashMap<String, WsContext>()

    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is StartGameMessage -> {
                val script = ScriptBuilderService.buildScriptForPlayerCount(usernameMap.size)
                val roles = ScriptBuilderService.getRolesInScript(script)

                // randomly assign roles
                tipuSession.setState(
                    TIPUGame(
                        (usernameMap.entries.shuffled() zip roles)
                            .map { TIPUPlayer(it.first.key, it.second, it.first.value) },
                        script,
                        tipuSession
                    )
                )


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