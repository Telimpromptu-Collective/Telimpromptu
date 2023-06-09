package teleimpromptu.script.parsing

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import teleimpromptu.TIPURole

enum class SegmentTag {
    INTRODUCTION, MAIN_STORY, SEGMENT, CLOSING
}

@Serializable
open class RawScriptSection(
    val tags: List<SegmentTag>,
    val primaryRoles: Set<TIPURole>,
    val lines: List<ScriptLine>,
    val prompts: List<ScriptPrompt>,
)

class ScriptSection(
    tags: List<SegmentTag>,
    primaryRoles: Set<TIPURole>,
    lines: List<ScriptLine>,
    prompts: List<ScriptPrompt>,
    val rolesInSection: Set<TIPURole>
): RawScriptSection(tags, primaryRoles, lines, prompts)

@Serializable
class ScriptLine(
    val speaker: TIPURole,
    val text: String
)

// scriptprompts are prompts from scripts, prompts are any type of prompt (including adlib prompt)
@Polymorphic
@Serializable(with = PromptSerializer::class)
sealed interface ScriptPrompt {
    fun unpack(): List<SinglePrompt>
}

@Serializable
class SinglePrompt(
    val id: String,
    val description: String
): ScriptPrompt {
    override fun unpack(): List<SinglePrompt> {
        return listOf(this)
    }
}

@Serializable
class PromptGroup(
    val groupId: String,
    val subPrompts: List<SinglePrompt>
): ScriptPrompt {
    override fun unpack(): List<SinglePrompt> {
        return this.subPrompts
    }
}

object PromptSerializer : JsonContentPolymorphicSerializer<ScriptPrompt>(ScriptPrompt::class) {
    override fun selectDeserializer(
        element: JsonElement
    ): DeserializationStrategy<ScriptPrompt> {
        return if (element.jsonObject.containsKey("groupId")) {
            PromptGroup.serializer()
        } else if (element.jsonObject.containsKey("id")) {
            SinglePrompt.serializer()
        } else {
            error("could not parse prompt ${element.jsonObject}")
        }
    }
}
