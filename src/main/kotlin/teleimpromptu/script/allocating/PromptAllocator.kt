package teleimpromptu.script.allocating

import teleimpromptu.states.promptAnswering.TIPUPromptAnsweringPlayer
import teleimpromptu.script.building.DetailedPromptBuilderService
import teleimpromptu.script.parsing.*

class PromptAllocator(private val players: List<TIPUPromptAnsweringPlayer>,
                      private val script: List<ScriptSection>) {

    // these are partially redundant...
    private val promptsToDoleOutWithUnresolvedDependencies: MutableList<DetailedPrompt>
    private val promptsToDoleOut: MutableList<DetailedPrompt>
    private val completedPromptIds: MutableList<String> = mutableListOf()

    private val promptsGivenToPlayer: MutableMap<TIPUPromptAnsweringPlayer, MutableList<SinglePrompt>> =
        players.associateWith { mutableListOf<SinglePrompt>() }.toMutableMap()

    init {
        val allDetailedPrompts: List<DetailedPrompt> = DetailedPromptBuilderService.buildDetailedScriptPrompts(script)
        // the list we start handing out only contains prompts with no dependencies.
        this.promptsToDoleOut = allDetailedPrompts.filter { it.dependentPrompts.isEmpty() }.toMutableList()
        this.promptsToDoleOutWithUnresolvedDependencies =
            allDetailedPrompts.filter { it.dependentPrompts.isNotEmpty() }.toMutableList()
    }

    fun moveCompletedPromptsAndAllocate(newlyCompletedPromptIds: List<String>, completedByPrompt: TIPUPromptAnsweringPlayer): Map<TIPUPromptAnsweringPlayer, List<SinglePrompt>> {
        moveCompletedPrompts(newlyCompletedPromptIds, completedByPrompt)
        return allocateAvailablePrompts()
    }

    // todo build this out into multiple strategies... this one is probably called flood
    fun allocateAvailablePrompts(): Map<TIPUPromptAnsweringPlayer, List<SinglePrompt>> {
        val allocatedPrompts: MutableMap<TIPUPromptAnsweringPlayer, MutableList<SinglePrompt>> = mutableMapOf()

        // give all the prompts out
        while (promptsToDoleOut.isNotEmpty()) {
            val detailedPrompt = promptsToDoleOut.removeFirst()

            // wont be null because we will never have 0 players
            val playerInNeed = promptsGivenToPlayer.entries
                // filter out speakers of this prompt
                .filter { !detailedPrompt.shouldNotBeGivenTo.contains(it.key.role) }
                // get the player with the fewest given prompts
                .minByOrNull { it.value.size }?.key

                // if there is no one who doesnt speak it just give it to someone
                ?: promptsGivenToPlayer.entries.minByOrNull { it.value.size }!!.key

            if (!allocatedPrompts.contains(playerInNeed)) {
                allocatedPrompts[playerInNeed] = mutableListOf()
            }

            allocatedPrompts[playerInNeed]!!.addAll(detailedPrompt.prompt.unpack())

            // update prompts given to player here so the info is updated for the next time in the loop
            promptsGivenToPlayer[playerInNeed]!!.addAll(detailedPrompt.prompt.unpack())
        }

        // convert the map to immutable lists
        return allocatedPrompts.entries.associate { it.key to it.value.toList() }
    }

    private fun moveCompletedPrompts(newlyCompletedPromptIds: List<String>, completedByPlayer: TIPUPromptAnsweringPlayer) {
        // move newlyCompletedPromptIds to completed
        for (promptList in promptsGivenToPlayer.values) {
            // remove completed prompts
            promptList.removeIf { newlyCompletedPromptIds.contains(it.id) }

            // if any of the prompts with dependencies have dependent prompts that
            // either have a completed prompt as a dependency, or in the case of a prompt group,
            // have a prompt that has one of the completed prompt ids as a dependency,
            // add the user that completed this prompt to that list... phew...
            promptsToDoleOutWithUnresolvedDependencies.filter {
                it.dependentPrompts.any {
                        dependentPrompt -> dependentPrompt.unpack()
                            .any { unpackedPrompt -> newlyCompletedPromptIds.contains(unpackedPrompt.id) }
                }
            }.forEach { it.shouldNotBeGivenTo.add(completedByPlayer.role) }
        }

        completedPromptIds.addAll(newlyCompletedPromptIds)

        // move prompts with all resolved dependencies to the promptstodoleout
        val promptsToMove = promptsToDoleOutWithUnresolvedDependencies.filter { areAllPromptDependenciesResolved(it) }
        promptsToDoleOutWithUnresolvedDependencies.removeAll(promptsToMove)
        promptsToDoleOut.addAll(promptsToMove)
    }

    fun outstandingPromptsForPlayer(player: TIPUPromptAnsweringPlayer): List<SinglePrompt> {
        return promptsGivenToPlayer[player]!!
    }

    fun areAllPromptsComplete(): Boolean {
        return promptsToDoleOutWithUnresolvedDependencies.isEmpty() &&
                promptsToDoleOut.isEmpty() &&
                promptsGivenToPlayer.all { it.value.isEmpty() }
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
                else -> {
                    error("prompt was of unknown type when checking if dependencies were resolved")
                }
            }
        }
    }
}