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
                if (usernameMap.size < 3) {
                    ctx.send(jsonDecoder.encodeToString(ErrorMessage("You need at least 3 players to start a game!")))
                    return
                }

                val script = ScriptBuilderService.buildScriptForPlayerCount(usernameMap.size)
                val roles = ScriptBuilderService.getRolesInScript(script)

                val players = (usernameMap.entries.shuffled() zip roles)
                    .map { TIPUPlayer(it.first.key, it.second, it.first.value) }

                // randomly assign roles
                tipuSession.state =
                    TIPUGame(
                        players,
                        script,
                        tipuSession
                    )

                val json = jsonDecoder
                    .encodeToString(GameStartedMessage(players.map { IngamePlayerStatus(it.username, it.role.toLowercaseString()) }))

                usernameMap.values.forEach { ws ->
                    ws.send(json)
                }
            }
            is CreateUserMessage -> {
                // if someone already connected with this username kick them and set this as the new one lol
                usernameMap[message.username]?.closeSession()
                usernameMap[message.username] = ctx

                ctx.send(jsonDecoder.encodeToString(ConnectionSuccessMessage()))

                updateUserStatuses()
            }
            else -> println("fail lobby: $message")
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