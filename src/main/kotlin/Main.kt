import gg.jte.CodeResolver
import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.resolve.ResourceCodeResolver
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.rendering.template.JavalinJte
import io.javalin.websocket.WsContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import teleimpromptu.TIPUSession
import teleimpromptu.message.Message
import teleimpromptu.states.TIPUGame


val games = mutableMapOf<String, TIPUSession>()

val Context.gameId: String get() = this.pathParam("game-id")
val WsContext.gameId: String get() = this.pathParam("game-id")

val jsonDecoder = Json { encodeDefaults = true }

fun main() {
    // config jte rendering
    val codeResolver: CodeResolver = ResourceCodeResolver("templates")
    val templateEngine = TemplateEngine.create(codeResolver, ContentType.Html)

    JavalinJte.init(templateEngine)

    // set up server
    Javalin.create {
        // it.staticFiles.add("/public")
        it.staticFiles.add("/app/build/static")
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
                try {
                    if (!games.containsKey(ctx.gameId)) {
                        // TODO send back error
                        return@onMessage
                    }

                    // println(ctx.message())
                    val message = jsonDecoder.decodeFromString<Message>(ctx.message())
                        ?: // TODO send back error
                        return@onMessage

                    games[ctx.gameId]!!.receiveMessage(ctx, message)
                } catch (ex: Exception) {
                    println("encountered exception in onmessage:\n${ex.message}")
                }
            }
        }
        get("/games/{game-id}/teleprompter") { ctx ->
            when (val gameState = games[ctx.gameId]?.state) {
                is TIPUGame -> {
                    ctx.render("teleprompter.jte", mapOf(
                        "script" to gameState.getFullFormattedScript(),
                        "roleMap" to gameState.getPlayers().associate { it.role to it.username }
                    ))
                }
            }
        }
    }.start(9090)
}
