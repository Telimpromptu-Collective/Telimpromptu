package teleimpromptu.script.parsing

import kotlinx.serialization.Serializable
import teleimpromptu.TIPURole

enum class SegmentTag {
    INTRODUCTION, MAIN_STORY, SEGMENT, CLOSING
}

@Serializable
class ScriptSection(
    val tags: List<SegmentTag>,
    val lines: List<ScriptLine>,
    val prompts: List<ScriptPrompt>,
) {
    fun getSpeakingRoles(): List<TIPURole> {
        return lines.map { it.speaker }.distinct()
    }
}

@Serializable
class ScriptLine(
    val speaker: TIPURole,
    val text: String
)

@Serializable
class ScriptPrompt(
    val id: String,
    val description: String
)