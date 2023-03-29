package teleimpromptu.script.allocating

import kotlinx.serialization.Serializable


open class Prompt

@Serializable
class AdlibPrompt(
    val id: String
): Prompt()