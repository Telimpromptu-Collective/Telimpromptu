package teleimpromptu.message

import kotlinx.serialization.Serializable
import teleimpromptu.script.parsing.ScriptPrompt


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
data class GameStartedMessage(
    val type: String = "gameStarted"
)

// ingame

@Serializable
data class NewPromptsMessage(
    val prompts: Collection<ScriptPrompt>,
    val type: String = "newPrompts"
)