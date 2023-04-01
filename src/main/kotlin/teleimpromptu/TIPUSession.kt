package teleimpromptu

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.message.HeartbeatMessage
import teleimpromptu.message.Message
import teleimpromptu.states.TIPULobby

class TIPUSession(val id: String) {
    var state: TIPUSessionState = TIPULobby(this)

    fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            is HeartbeatMessage -> {
                // println("beep...")
            }
            else -> state.receiveMessage(ctx, message)
        }
    }

    fun receiveDisconnect(ctx: WsCloseContext) {
        state.receiveDisconnect(ctx)
    }
}