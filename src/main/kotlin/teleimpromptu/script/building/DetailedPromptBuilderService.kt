package teleimpromptu.script.building

import teleimpromptu.TIPURole
import teleimpromptu.script.allocating.DetailedPrompt
import teleimpromptu.script.parsing.*

object DetailedPromptBuilderService {
    // todo this can be changed to happen at json parse time
    // we should try to maintain the order of the prompts that they are in inside the config
    // so we can serve them in roughly that order
    fun buildDetailedScriptPrompts(script: List<ScriptSection>): MutableList<DetailedPrompt> {
        val prompts: MutableList<DetailedPrompt> = mutableListOf()


        val promptMap: Map<String, ScriptPrompt> = getMapOfPromptsFromScript(script)
        val regex = Regex("""\{\!(.*?)\}""") // matches "{!id}" pattern


        for (scriptSection: ScriptSection in script) {
            for (scriptPrompt: ScriptPrompt in scriptSection.prompts) {
                val speakers: MutableList<TIPURole> = mutableListOf()
                val dependentPrompts: MutableList<ScriptPrompt> = mutableListOf()

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

                prompts.add(DetailedPrompt(scriptPrompt, speakers, dependentPrompts.toList()))
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
}