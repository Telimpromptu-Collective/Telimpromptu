package teleimpromptu.script.building

import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptParsingService
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SegmentTag

object ScriptBuilderService {
    fun buildScriptForPlayerCount(playerCount: Int): MutableList<ScriptSection> {
        val script = mutableListOf<ScriptSection>()
        script.add(getAvailableSections(playerCount, SegmentTag.INTRODUCTION, script).random())

        do {
            // protect against infinite looping here
            script.add(getAvailableSections(playerCount, SegmentTag.SEGMENT, script).random())
        } while (getPrimaryRolesInScript(script).size < playerCount)

        if (getPrimaryRolesInScript(script).size > playerCount) {
            error("Script was generated with too many roles!?")
        }

        if (getRolesInScript(script) != getPrimaryRolesInScript(script)) {
            error("There are roles in the script without sections where they are the primary role!?")
        }

        script.add(getAvailableSections(playerCount, SegmentTag.CLOSING, script, true).random())

        return script
    }

    private fun getAvailableSections(
        playerCount: Int,
        filterByTag: SegmentTag,
        usedSections: List<ScriptSection>,
        canAddNothing: Boolean = false
    ): List<ScriptSection> {
        return ScriptParsingService.sections
            .filter { it.tags.contains(filterByTag) }
            .filter { c ->
                val isNewRoleAdded = !usedSections
                    .map { it.rolesInSection }
                    .flatten()
                    .toSet()
                    .containsAll(c.rolesInSection)
                isNewRoleAdded || canAddNothing
            }
            .filter {
                val usedRoles = usedSections.map { it.rolesInSection }.flatten().toMutableSet()
                usedRoles.addAll(it.primaryRoles)
                usedRoles.size <= playerCount
            }
    }

    fun getRolesInScript(script: List<ScriptSection>): Set<TIPURole> {
        return script.flatMap { it.rolesInSection }.toSet()
    }

    fun getPrimaryRolesInScript(script: List<ScriptSection>): Set<TIPURole> {
        return script.flatMap { it.primaryRoles }.toSet()
    }
}