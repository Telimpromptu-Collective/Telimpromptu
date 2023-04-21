package teleimpromptu.script.formatting

import teleimpromptu.states.promptAnswering.TIPUPromptAnsweringPlayer

class PromptFormatter(private val players: List<TIPUPromptAnsweringPlayer>) {
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
            formattedText = formattedText.replace("{@${player.role.toLowercaseString()}lastname}",
                player.lastname)
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
