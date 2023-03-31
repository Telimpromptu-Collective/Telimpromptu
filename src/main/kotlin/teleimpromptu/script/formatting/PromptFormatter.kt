package teleimpromptu.script.formatting

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole

class PromptFormatter(val players: List<TIPUPlayer>) {
    private val formatMap: MutableMap<String, String> = mutableMapOf()

    // todo this sucks
    fun formatText(text: String): String {
        var formattedText = text
        for (entry in formatMap) {
            val promptId = entry.key
            val response = entry.value

            formattedText = formattedText.replace("{!${promptId}}", response)
        }

        for (role in TIPURole.values()) {
            formattedText = formattedText.replace("{@${role.toLowercaseString()}",
                players.find { it.role == role }!!.username )
        }

        return formattedText
    }

    fun addPromptResponse(promptId: String, response: String) {
        formatMap[promptId] = response
    }

    // todo
    enum class ResponseType {
        STANDARD, ADLIB
    }
}
