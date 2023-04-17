package teleimpromptu.states.storySelection

import io.javalin.websocket.WsContext

data class TIPUStorySelectionPlayer(val username: String, var connection: WsContext) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is TIPUStorySelectionPlayer) {
            return false
        } else {
            if (other.username == this.username) {
                return true
            }
        }

        return false
    }

    override fun hashCode(): Int {
        return this.username.hashCode()
    }
}