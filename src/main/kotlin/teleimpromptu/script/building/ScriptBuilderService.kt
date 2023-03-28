package teleimpromptu.script.building

import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptParsingService
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SegmentTag

object ScriptBuilderService {
    fun buildScriptForPlayerCount(playerCount: Int): MutableList<ScriptSection> {
        val script = mutableListOf<ScriptSection>()
        script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.INTRODUCTION, script).random())

        do {
            // protect against infinite looping here
            script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.SEGMENT, script).random())
        } while (getRolesInScript(script).size >= playerCount)

        if (getRolesInScript(script).size > playerCount) {
            error("Something has gone horribly wrong.... Script was generated with too many roles.")
        }

        script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.CLOSING, script).random())

        return script
    }

    private fun getAvailableSectionsForRoles(playerCount: Int,
                                             filterByTag: SegmentTag,
                                             scriptSoFar: List<ScriptSection>): List<ScriptSection> {
        val rolesInScript = getRolesInScript(scriptSoFar)
        return ScriptParsingService.sections
            // if it is the type we are looking for
            .filter { it.tags.contains(filterByTag) }

            // and it adds at least one role
            // AND the new roles it would add to the script would still be less than our player count
            .filter {
                val newRoleCount = getNewRoles(it.getSpeakingRoles(), rolesInScript).size
                return@filter newRoleCount > 1 &&
                        newRoleCount + rolesInScript.size <= playerCount
            }
    }

    private fun getNewRoles(newRolesFromSection: List<TIPURole>, preexisingRoles: List<TIPURole>): List<TIPURole> {
        return newRolesFromSection.filter { !preexisingRoles.contains(it) }
    }

    fun getRolesInScript(script: List<ScriptSection>): List<TIPURole> {
        return script.flatMap { it.getSpeakingRoles() }.distinct()
    }
}