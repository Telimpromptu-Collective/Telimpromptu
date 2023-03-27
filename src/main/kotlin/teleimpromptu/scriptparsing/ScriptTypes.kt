package teleimpromptu.scriptparsing

import teleimpromptu.TIPURole

enum class SegmentTag {
    INTRODUCTION, MAIN_STORY, SEGMENT, CLOSING
}
class ScriptSection(
    val tags: List<SegmentTag>,
    val lines: List<ScriptLine>,
    val prompts: List<ScriptPrompt>,
) {
    fun getSpeakingRoles(): List<TIPURole> {
        return lines.map { it.speaker }.distinct()
    }
}

class ScriptLine(
    val speaker: TIPURole,
    val text: String
)

class ScriptPrompt(
    val id: String,
    val description: String
)