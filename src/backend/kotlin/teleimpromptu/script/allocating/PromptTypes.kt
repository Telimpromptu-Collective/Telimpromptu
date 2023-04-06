package teleimpromptu.script.allocating

import kotlinx.serialization.Serializable
import teleimpromptu.script.parsing.SinglePrompt


open interface Prompt {
    fun unpack(): List<SinglePrompt>
}

@Serializable
class AdlibPrompt(
    val id: String
): Prompt {
    override fun unpack(): List<SinglePrompt> {
        TODO("Not yet implemented")
    }
}