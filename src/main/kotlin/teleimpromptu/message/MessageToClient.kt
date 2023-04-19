package teleimpromptu.message

import kotlinx.serialization.Serializable
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
    val username: String,
    val type: String = "connectionSuccess"
)

@Serializable
data class IngamePlayerStatus(
    val username: String,
    val role: String
)

// ingame

@Serializable
data class NewPromptsMessage(
    val scriptPrompts: Collection<SinglePrompt>,
    val type: String = "newPrompts"
)

@Serializable
data class PromptsCompleteMessage(
    val type: String = "promptsComplete"
)

// storysubmission


@Serializable
data class EnterPromptAnsweringStateMessage(
    val storyOfTheNight: String,
    val statuses: Collection<IngamePlayerStatus>,
    val type: String = "enterPromptAnsweringState"
)

@Serializable
data class StoryForClient(
    val author: String?,
    val story: String,
    val voters: List<String>
)

@Serializable
data class StoryVotingStateUpdateMessage(
    val stories: List<StoryForClient>,
    val type: String = "storyVotingStateUpdate"
)

@Serializable
data class EnterStoryVotingStateMessage(
    val type: String = "enterStoryVotingState"
)