package teleimpromptu

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import teleimpromptu.message.Message
import teleimpromptu.message.UserConnectMessage

interface TIPUSessionState {
    fun receiveMessage(ctx: WsMessageContext, message: Message)
    fun recieveConnectionMessage(ctx: WsMessageContext, message: UserConnectMessage)
    fun receiveDisconnect(ctx: WsCloseContext)
}