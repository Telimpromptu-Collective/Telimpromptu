package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptPrompt
import teleimpromptu.script.parsing.ScriptSection

class PromptAllocator(private val players: List<TIPUPlayer>,
                      private val script: List<ScriptSection>) {

    private val remainingPrompts: MutableList<DetailedScriptPrompt> = buildDetailedScriptPrompts()

    private val playerQueue: MutableList<TIPUPlayer> = players.shuffled().toMutableList()

    fun buildDetailedScriptPrompts(): MutableList<DetailedScriptPrompt> {
        val prompts: MutableList<DetailedScriptPrompt> = mutableListOf()

        for (scriptSection : ScriptSection in script) {
            for (prompt : ScriptPrompt in scriptSection.prompts) {
                val speakers: MutableList<TIPURole> = mutableListOf()

                for (line : ScriptLine in scriptSection.lines) {
                    if (line.text.contains("{!${prompt.id}")) {
                        speakers.add(line.speaker)
                    }
                }
                prompts.add(DetailedScriptPrompt(prompt.id, prompt.description, speakers.toList()))
            }

        }
        return prompts
    }

    fun allocateAvailablePrompts() {

    }
}