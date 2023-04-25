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
import teleimpromptu.states.storySelection.TIPUStorySelectionPlayer
import teleimpromptu.states.storySelection.TIPUStoryVotingState
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class TIPULobbyState(private val tipuSession: TIPUSession) : TIPUSessionState {
    private val usernameMap = ConcurrentHashMap<String, WsContext>()
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is StartGameMessage -> {
                if (usernameMap.size < 3) {
                    ctx.send(jsonDecoder.encodeToString(ErrorMessage("You need at least 3 players to start a game!")))
                    return
                }

                tipuSession.state = TIPUStoryVotingState(
                    usernameMap.entries.map { TIPUStorySelectionPlayer(it.key, it.value) },
                    tipuSession)
            }
            else -> println("fail lobby: $message")
        }
    }

    override fun recieveConnectionMessage(ctx: WsMessageContext, message: UserConnectMessage) {
        // if someone already connected with this username kick them and set this as the new one lol
        usernameMap[message.username]?.closeSession()
        usernameMap[message.username] = ctx

        ctx.send(jsonDecoder.encodeToString(ConnectionSuccessMessage(message.username)))

        updateUserStatuses()
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
                UsernameStatus(entry.key, !entry.value.session.isOpen)
            }
        )

        val json = jsonDecoder.encodeToString(updateMessage)

        usernameMap.values.forEach { ws ->
            ws.send(json)
        }
    }
}