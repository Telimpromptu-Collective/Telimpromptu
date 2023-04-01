package teleimpromptu.message

import kotlinx.serialization.Serializable
import teleimpromptu.script.allocating.AdlibPrompt
import teleimpromptu.script.parsing.ScriptPrompt
import teleimpromptu.script.parsing.SinglePrompt


// general
@Serializable
data class ErrorMessage(
    val message: String,
    val type: String = "error"
)

// lobby
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

@Serializable
data class IngamePlayerStatus(
    val username: String,
    val role: String
)

@Serializable
data class GameStartedMessage(
    val statuses: Collection<IngamePlayerStatus>,
    val type: String = "gameStarted"
)

// ingame

@Serializable
data class NewPromptsMessage(
    val scriptPrompts: Collection<SinglePrompt>,
    val adlibPrompts: Collection<AdlibPrompt>,
    val type: String = "newPrompts"
)