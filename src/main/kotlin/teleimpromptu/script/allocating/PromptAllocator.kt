package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.*

class PromptAllocator(private val players: List<TIPUPlayer>,
                      private val script: List<ScriptSection>) {

    private val prompt: MutableList<DetailedPrompt> = buildDetailedScriptPrompts()
    private val playerQueue: MutableList<TIPUPlayer> = players.shuffled().toMutableList()

    // we should try to maintain the order of the prompts that they are in inside the config
    // so we can serve them in roughly that order
    private fun buildDetailedScriptPrompts(): MutableList<DetailedPrompt> {
        val prompts: MutableList<DetailedPrompt> = mutableListOf()
        val promptMap: Map<String, ScriptPrompt> = getMapOfPromptsFromScript(script)
        val regex = Regex("""\{\!(.*?)\}""") // matches "{!id}" pattern


        for (scriptSection: ScriptSection in script) {
            for (scriptPrompt: ScriptPrompt in scriptSection.prompts) {
                val speakers: MutableList<TIPURole> = mutableListOf()
                val dependentPrompts: MutableList<Prompt> = mutableListOf()

                // get dependent prompts
                when (scriptPrompt) {
                    is PromptGroup -> {
                        for (subPrompt: SinglePrompt in scriptPrompt.subPrompts) {
                            val idMatches = regex.findAll(subPrompt.description)
                            for (idMatch in idMatches) {
                                promptMap[idMatch.groupValues[1]]?.let { dependentPrompts.add(it) }
                            }
                        }
                    }
                    is SinglePrompt -> {
                        val idMatches = regex.findAll(scriptPrompt.description)
                        for (idMatch in idMatches) {
                            promptMap[idMatch.groupValues[1]]?.let { dependentPrompts.add(it) }
                        }
                    }
                }

                // get speakers
                for (line : ScriptLine in scriptSection.lines) {
                    when (scriptPrompt) {
                        is PromptGroup -> {
                            for (subPrompt: SinglePrompt in scriptPrompt.subPrompts) {
                                if (line.text.contains("{!${subPrompt.id}")) {
                                    speakers.add(line.speaker)
                                }
                            }
                        }
                        is SinglePrompt -> {
                            if (line.text.contains("{!${scriptPrompt.id}")) {
                                speakers.add(line.speaker)
                            }
                        }
                    }
                }

                prompts.add(DetailedPrompt(scriptPrompt, speakers.toList(), dependentPrompts.toList()))
            }

        }
        return prompts
    }

    private fun getMapOfPromptsFromScript(script: List<ScriptSection>): Map<String, ScriptPrompt> {
        val promptMap: MutableMap<String, ScriptPrompt> = mutableMapOf()
        for (scriptSection: ScriptSection in script) {
            for (scriptPrompt: ScriptPrompt in scriptSection.prompts) {
                when (scriptPrompt) {
                    is PromptGroup -> {
                        for (subPrompt: SinglePrompt in scriptPrompt.subPrompts) {
                            promptMap[subPrompt.id] = subPrompt
                        }
                    }
                    is SinglePrompt -> {
                        promptMap[scriptPrompt.id] = scriptPrompt
                    }
                }
            }
        }
        return promptMap.toMap()
    }

    fun allocateAvailablePrompts() {

    }
}