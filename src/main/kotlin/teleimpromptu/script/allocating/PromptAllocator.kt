package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.*

class PromptAllocator(private val players: List<TIPUPlayer>,
                      private val script: List<ScriptSection>) {

    private val promptsToDoleOutWithUnresolvedDependencies: MutableList<DetailedPrompt>
    private val promptsToDoleOut: MutableList<DetailedPrompt>
    private val givenPromptIds: MutableList<String> = mutableListOf()
    private val completedPromptIds: MutableList<String> = mutableListOf()

    private val promptsGivenToPlayer: MutableMap<TIPUPlayer, Int> = players.associateWith { 0 }.toMutableMap()

    init {
        val allDetailedPrompts = buildDetailedScriptPrompts()
        // the list we start handing out only contains prompts with no dependencies.
        this.promptsToDoleOut = allDetailedPrompts.filter { it.dependentPrompts.isEmpty() }.toMutableList()
        this.promptsToDoleOutWithUnresolvedDependencies =
            allDetailedPrompts.filter { it.dependentPrompts.isNotEmpty() }.toMutableList()
    }

    // todo build this out into multiple strategies... this one is probably called flood
    fun allocateAvailablePrompts(newlyCompletedPromptIds: List<String>): Map<TIPUPlayer, List<Prompt>> {
        val allocatedPrompts: MutableMap<TIPUPlayer, MutableList<Prompt>> = mutableMapOf()

        // move these ids to completed
        newlyCompletedPromptIds.forEach { givenPromptIds.remove(it) }
        completedPromptIds.addAll(newlyCompletedPromptIds)

        // move prompts with all resolved dependencies to the promptstodoleout
        val promptsToMove = promptsToDoleOutWithUnresolvedDependencies.filter { areAllPromptDependenciesResolved(it) }
        promptsToDoleOutWithUnresolvedDependencies.removeAll(promptsToMove)
        promptsToDoleOut.addAll(promptsToMove)


        // give all the prompts out
        while (promptsToDoleOut.isNotEmpty()) {
            val detailedPrompt = promptsToDoleOut.removeFirst()

            // wont be null because we will never have 0 players
            val playerInNeed = promptsGivenToPlayer.entries
                // filter out speakers of this prompt
                .filter { !detailedPrompt.speakers.contains(it.key.role) }
                .minByOrNull { it.value }!!.key

            if (!allocatedPrompts.contains(playerInNeed)) {
                allocatedPrompts[playerInNeed] = mutableListOf()
            }

            allocatedPrompts[playerInNeed]!!.add(detailedPrompt.prompt)
            promptsGivenToPlayer[playerInNeed] = promptsGivenToPlayer[playerInNeed]!! + 1
        }

        // convert the map to immutable lists
        return allocatedPrompts.entries.associate { it.key to it.value.toList() }
    }

    fun addAdlibPrompt(prompt: AdlibPrompt) {
        // todo we could add speakers here but it probably doesnt matter
        promptsToDoleOut.add(DetailedPrompt(prompt, listOf(), listOf()))
    }

    private fun areAllPromptDependenciesResolved(prompt: DetailedPrompt): Boolean {
        return prompt.dependentPrompts.all {
            when (it) {
                is SinglePrompt -> {
                    completedPromptIds.contains(it.id)
                }
                is PromptGroup -> {
                    it.subPrompts.all { subPrompt -> completedPromptIds.contains(subPrompt.id) }
                }
                is AdlibPrompt -> {
                    completedPromptIds.contains(it.id)
                }
                else -> {
                    error("prompt was of unknown type when checking if dependencies were resolved")
                }
            }
        }
    }

    // todo this can be changed to parse time pretty sure
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
}