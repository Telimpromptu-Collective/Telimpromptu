package teleimpromptu

import io.javalin.websocket.WsContext
import java.util.Objects

data class TIPUPlayer(val username: String, val role: TIPURole, val lastname: String, var connection: WsContext) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is TIPUPlayer) {
            return false
        } else {
            if (other.username == this.username && other.role == this.role) {
                return true
            }
        }

        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(this.username, this.role)
    }
}