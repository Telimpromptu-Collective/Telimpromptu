package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection

class PromptAllocator(private val players: List<TIPUPlayer>,
                      private val script: List<ScriptSection>) {

    private val remainingPrompts: MutableList<DetailedScriptPrompt> = TODO()

    private val playerQueue: MutableList<TIPUPlayer> = players.shuffled().toMutableList()

    fun buildDetailedScriptPrompts() {
        val prompts: MutableList<DetailedScriptPrompt> = mutableListOf()
        for (scriptSection: ScriptSection in script) {
            for (line: ScriptLine in scriptSection.lines) {

            }
        }
    }

    fun allocateAvailablePrompts() {

    }
}