package teleimpromptu

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.message.Message
import teleimpromptu.states.TIPULobby

class TIPUSession(val id: String) {
    private var state: TIPUSessionState = TIPULobby(this)

    fun receiveMessage(ctx: WsMessageContext, message: Message) {
        when (message) {
            else -> state.receiveMessage(ctx, message)
        }
    }

    fun setState(newState: TIPUSessionState) {
        state = newState
    }

    fun receiveDisconnect(ctx: WsCloseContext) {
        state.receiveDisconnect(ctx)
    }
}