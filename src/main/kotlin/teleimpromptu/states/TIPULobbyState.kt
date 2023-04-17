package teleimpromptu.states

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.states.promptAnswering.TIPUPromptAnsweringPlayer
import teleimpromptu.TIPURole
import teleimpromptu.TIPUSession
import teleimpromptu.TIPUSessionState
import teleimpromptu.message.*
import teleimpromptu.script.building.ScriptBuilderService
import teleimpromptu.states.promptAnswering.TIPUPromptAnsweringState
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class TIPULobbyState(private val tipuSession: TIPUSession) : TIPUSessionState {
    private val usernameMap = ConcurrentHashMap<String, WsContext>()

    private val lastNameMap = mapOf(
        TIPURole.HOST to listOf("Newsly", "Hostman", "Hostdanews"),
        TIPURole.COHOST to listOf("McNewsman", "Newsperson", "Hosterson"),
        TIPURole.GUESTEXPERT to listOf("Expertson", "Knowsalot", "McQualified"),
        TIPURole.DETECTIVE to listOf("Gumshoe", "McSniff", "Sleuthburger"),
        TIPURole.FIELDREPORTER to listOf("Reportson", "McReporter", "Rerpotsalot"),
        TIPURole.WITNESS to listOf("Realman", "Eyeball"),
        TIPURole.COMMENTATOR to listOf("Smith"),
        TIPURole.ZOOKEEPER to listOf("Zooman", "King", "Animalman"),
        TIPURole.RELIGIOUSLEADER to listOf("Smith")
    )
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is StartGameMessage -> {
                if (usernameMap.size < 3) {
                    ctx.send(jsonDecoder.encodeToString(ErrorMessage("You need at least 3 players to start a game!")))
                    return
                }

                val script = ScriptBuilderService.buildScriptForPlayerCount(usernameMap.size)
                val roles = ScriptBuilderService.getPrimaryRolesInScript(script)

                val players = (usernameMap.entries.shuffled() zip roles)
                    .map { (entry, role) ->
                        val lastNameList = lastNameMap[role] ?: error("No last name for $role")
                        val lastName = lastNameList[Random.nextInt(lastNameList.size)]
                        TIPUPromptAnsweringPlayer(entry.key, role, lastName, entry.value)
                    }

                // randomly assign roles
                tipuSession.state =
                    TIPUPromptAnsweringState(
                        players,
                        script,
                        tipuSession
                    )
            }
            is UserConnectMessage -> {
                // if someone already connected with this username kick them and set this as the new one lol
                usernameMap[message.username]?.closeSession()
                usernameMap[message.username] = ctx

                ctx.send(jsonDecoder.encodeToString(ConnectionSuccessMessage(message.username)))

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