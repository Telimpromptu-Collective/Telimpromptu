package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.*

class PromptAllocator(private val players: List<TIPUPlayer>,
                      private val script: List<ScriptSection>) {

    // these are partially redundant...
    private val promptsToDoleOutWithUnresolvedDependencies: MutableList<DetailedPrompt>
    private val promptsToDoleOut: MutableList<DetailedPrompt>
    private val completedPromptIds: MutableList<String> = mutableListOf()

    private val promptsGivenToPlayer: MutableMap<TIPUPlayer, MutableList<SinglePrompt>> =
        players.associateWith { mutableListOf<SinglePrompt>() }.toMutableMap()

    init {
        val allDetailedPrompts = buildDetailedScriptPrompts()
        // the list we start handing out only contains prompts with no dependencies.
        this.promptsToDoleOut = allDetailedPrompts.filter { it.dependentPrompts.isEmpty() }.toMutableList()
        this.promptsToDoleOutWithUnresolvedDependencies =
            allDetailedPrompts.filter { it.dependentPrompts.isNotEmpty() }.toMutableList()
    }

    // todo build this out into multiple strategies... this one is probably called flood
    fun allocateAvailablePrompts(newlyCompletedPromptIds: List<String>): Map<TIPUPlayer, List<SinglePrompt>> {
        val allocatedPrompts: MutableMap<TIPUPlayer, MutableList<SinglePrompt>> = mutableMapOf()

        // move newlyCompletedPromptIds to completed
        for (promptList in promptsGivenToPlayer.values) {
            promptList.removeIf { newlyCompletedPromptIds.contains(it.id) }
        }

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
                // get the player with the fewest given prompts
                .minByOrNull { it.value.size }?.key

                // if there is no one who doesnt speak it just give it to someone
                ?: promptsGivenToPlayer.entries.minByOrNull { it.value.size }!!.key

            if (!allocatedPrompts.contains(playerInNeed)) {
                allocatedPrompts[playerInNeed] = mutableListOf()
            }

            allocatedPrompts[playerInNeed]!!.addAll(detailedPrompt.prompt.unpack())
            promptsGivenToPlayer[playerInNeed]!!.addAll(detailedPrompt.prompt.unpack())
        }

        // convert the map to immutable lists
        return allocatedPrompts.entries.associate { it.key to it.value.toList() }
    }

    fun outstandingPromptsForPlayer(player: TIPUPlayer): List<SinglePrompt> {
        return promptsGivenToPlayer[player]!!
    }

    fun areAllPromptsComplete(): Boolean {
        return promptsToDoleOutWithUnresolvedDependencies.isEmpty() &&
                promptsToDoleOut.isEmpty() &&
                promptsGivenToPlayer.all { it.value.isEmpty() }
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

    // todo this can be changed to happen at json parse time
    // we should try to maintain the order of the prompts that they are in inside the config
    // so we can serve them in roughly that order
    private fun buildDetailedScriptPrompts(): MutableList<DetailedPrompt> {
        val prompts: MutableList<DetailedPrompt> = mutableListOf()

        // todo is there a better place we can generate last name prompts...
        prompts.addAll(
            players.map { DetailedPrompt(
                SinglePrompt("${it.role.toLowercaseString()}_lastname",
                    "The last name for ${it.username} who is a ${it.role.toLowercaseString()}"
                ),
                listOf(it.role),
                listOf()
            )
            }
        )


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