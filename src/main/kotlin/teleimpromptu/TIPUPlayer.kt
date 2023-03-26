package teleimpromptu

import io.javalin.websocket.WsContext

data class TIPUPlayer(val username: String, val role: TIPURole, var connection: WsContext)