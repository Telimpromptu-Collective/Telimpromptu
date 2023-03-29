package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.*

class PromptAllocator(private val players: List<TIPUPlayer>,
                      private val script: List<ScriptSection>) {

    private val remainingPrompts: MutableList<DetailedScriptPrompt> = buildDetailedScriptPrompts()

    private val playerQueue: MutableList<TIPUPlayer> = players.shuffled().toMutableList()

    fun buildDetailedScriptPrompts(): MutableList<DetailedScriptPrompt> {
        val prompts: MutableList<DetailedScriptPrompt> = mutableListOf()
        val regex = Regex("""\{\!(.*?)\}""") // matches "{!id}" pattern

        for (scriptSection : ScriptSection in script) {

            // build list of DeatiledScriptPrompts, initially with empty speakers list
            for (prompt : ScriptPrompt in scriptSection.prompts) {
                when (prompt) {
                    is SinglePrompt -> {
                        prompts.add(DetailedScriptPrompt(SinglePrompt(prompt.id, prompt.description), mutableListOf()))
                    }

                    is PromptGroup -> TODO()
                }
            }

            // add speakers to the list
            for (line: ScriptLine in scriptSection.lines) {
                // search for {!id} in line
                if (regex.containsMatchIn(line.text)) {
                    val matchResult = regex.find(line.text)
                    val id = matchResult?.groupValues!![1] // extracts the id
                    for (detailedScriptPrompt : DetailedScriptPrompt in prompts) {
                        /*if (detailedScriptPrompt.id == id) {
                            detailedScriptPrompt.speakers.add(line.speaker)
                        }*/
                    }
                }
            }
        }
        return prompts
    }

    fun allocateAvailablePrompts() {

    }
}