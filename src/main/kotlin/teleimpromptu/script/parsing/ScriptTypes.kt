package teleimpromptu.script.parsing

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import teleimpromptu.TIPURole
import teleimpromptu.script.allocating.Prompt

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

// scriptprompts are prompts from scripts, prompts are any type of prompt (including adlib prompt)
@Polymorphic
@Serializable(with = PromptSerializer::class)
sealed class ScriptPrompt: Prompt()

@Serializable
class SinglePrompt(
    val id: String,
    val description: String
): ScriptPrompt()

@Serializable
class PromptGroup(
    val groupId: String,
    val subPrompts: List<SinglePrompt>
): ScriptPrompt()

object PromptSerializer : JsonContentPolymorphicSerializer<ScriptPrompt>(ScriptPrompt::class) {
    override fun selectDeserializer(
        element: JsonElement
    ): DeserializationStrategy<out ScriptPrompt> {
        return if (element.jsonObject.containsKey("groupId")) {
            PromptGroup.serializer()
        } else if (element.jsonObject.containsKey("id")) {
            SinglePrompt.serializer()
        } else {
            error("could not parse prompt ${element.jsonObject}")
        }
    }
}
