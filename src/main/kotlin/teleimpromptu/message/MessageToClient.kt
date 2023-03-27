package teleimpromptu.message

import kotlinx.serialization.Serializable

@Serializable
data class UsernameStatus(
    val username: String,
    val connected: Boolean
)
@Serializable
data class UsernameUpdateMessage(
    val statuses: Collection<UsernameStatus>,
    val type: String = "usernameUpdate"
)

@Serializable
data class ConnectionSuccessMessage(
    val type: String = "connectionSuccess"
)