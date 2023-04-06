package teleimpromptu.script.formatting

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole

class PromptFormatter(private val players: List<TIPUPlayer>) {
    private val formatMap: MutableMap<String, String> = mutableMapOf()

    // todo this sucks
    fun formatText(text: String): String {
        var formattedText = text
        for (entry in formatMap) {
            val promptId = entry.key
            val response = entry.value

            formattedText = formattedText.replace("{!${promptId}}", response)
        }

        for (player in players) {
            formattedText = formattedText.replace("{@${player.role.toLowercaseString()}}",
                player.username.replaceFirstChar(Char::titlecase))
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
