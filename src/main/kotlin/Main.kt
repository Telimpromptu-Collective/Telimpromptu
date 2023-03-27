import teleimpromptu.TIPUSession
import io.javalin.Javalin
import io.javalin.websocket.WsContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import teleimpromptu.message.Message
import teleimpromptu.scriptparsing.ScriptParsingService

val games = mutableMapOf<String, TIPUSession>()

val WsContext.gameId: String get() = this.pathParam("game-id")

val jsonDecoder = Json { encodeDefaults = true }

fun main() {
    Javalin.create {
        it.staticFiles.add("/public")
    }.apply {
        ws("/games/{game-id}") { ws ->
            ws.onConnect { ctx ->
                println("connection from ${ctx.session.remoteAddress}")

                if (!games.containsKey(ctx.gameId)) {
                    games[ctx.gameId] = TIPUSession(ctx.gameId)
                }
            }

            ws.onClose { ctx ->
                if (!games.containsKey(ctx.gameId)) {
                    // TODO send back error
                    return@onClose
                }

                games[ctx.gameId]!!.receiveDisconnect(ctx)
            }

            ws.onMessage { ctx ->
                if (!games.containsKey(ctx.gameId)) {
                    // TODO send back error
                    return@onMessage
                }

                // println(ctx.message())
                val message = jsonDecoder.decodeFromString<Message>(ctx.message())
                    ?: // TODO send back error
                    return@onMessage

                games[ctx.gameId]!!.receiveMessage(ctx, message)
            }
        }
    }.start(7070)
}
