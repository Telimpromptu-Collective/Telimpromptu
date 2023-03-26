package teleimpromptu

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.message.Message

interface TIPUSessionState {
    fun receiveMessage(ctx: WsMessageContext, message: Message)
    fun receiveDisconnect(ctx: WsCloseContext)
}