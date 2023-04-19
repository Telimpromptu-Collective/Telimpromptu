package teleimpromptu

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.message.HeartbeatMessage
import teleimpromptu.message.Message
import teleimpromptu.message.UserConnectMessage
import teleimpromptu.states.TIPULobbyState

class TIPUSession(val id: String) {
    var state: TIPUSessionState = TIPULobbyState(this)

    fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is HeartbeatMessage -> {
                // println("beep...")
            }
            // handle reconnects differently
            is UserConnectMessage -> {
                state.recieveConnectionMessage(ctx, message)
            }
            else -> state.receiveMessage(ctx, message)
        }
    }

    fun receiveDisconnect(ctx: WsCloseContext) {
        state.receiveDisconnect(ctx)
    }
}