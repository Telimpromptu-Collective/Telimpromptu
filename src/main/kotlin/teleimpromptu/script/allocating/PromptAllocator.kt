package teleimpromptu.script.allocating

import teleimpromptu.TIPUPlayer
import teleimpromptu.script.building.DetailedPromptBuilderService
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
        val allDetailedPrompts: List<DetailedPrompt> = DetailedPromptBuilderService.buildDetailedPrompts(script, players)
        // the list we start handing out only contains prompts with no dependencies.
        this.promptsToDoleOut = allDetailedPrompts.filter { it.dependentPrompts.isEmpty() }.toMutableList()
        this.promptsToDoleOutWithUnresolvedDependencies =
            allDetailedPrompts.filter { it.dependentPrompts.isNotEmpty() }.toMutableList()
    }

    fun moveCompletedPromptsAndAllocate(newlyCompletedPromptIds: List<String>): Map<TIPUPlayer, List<SinglePrompt>> {
        moveCompletedPrompts(newlyCompletedPromptIds)
        return allocateAvailablePrompts()
    }

    // todo build this out into multiple strategies... this one is probably called flood
    private fun allocateAvailablePrompts(): Map<TIPUPlayer, List<SinglePrompt>> {
        val allocatedPrompts: MutableMap<TIPUPlayer, MutableList<SinglePrompt>> = mutableMapOf()

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
            promptsGivenToPlayer[playerInNeed]!!.addAll(detailedPrompt.prompt.unpack())
        }

        // convert the map to immutable lists
        return allocatedPrompts.entries.associate { it.key to it.value.toList() }
    }

    private fun moveCompletedPrompts(newlyCompletedPromptIds: List<String>) {
        // val playerToCompletedPromptIds: MutableMap<TIPUPlayer, List<String>> = mutableMapOf()

        // move newlyCompletedPromptIds to completed
        for (promptList in promptsGivenToPlayer.values) {
            promptList.removeIf { newlyCompletedPromptIds.contains(it.id) }
        }

        completedPromptIds.addAll(newlyCompletedPromptIds)

        // move prompts with all resolved dependencies to the promptstodoleout
        val promptsToMove = promptsToDoleOutWithUnresolvedDependencies.filter { areAllPromptDependenciesResolved(it) }
        promptsToDoleOutWithUnresolvedDependencies.removeAll(promptsToMove)
        promptsToDoleOut.addAll(promptsToMove)
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
}