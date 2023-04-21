package teleimpromptu.script.building

import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptParsingService
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SegmentTag

object ScriptBuilderService {
    fun buildScriptForPlayerCount(playerCount: Int): MutableList<ScriptSection> {
        val script = mutableListOf<ScriptSection>()
        script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.INTRODUCTION, script).random())
        // script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.MAIN_STORY, script).random())

        do {
            // protect against infinite looping here
            script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.SEGMENT, script).random())
        } while (getPrimaryRolesInScript(script).size < playerCount)

        if (getPrimaryRolesInScript(script).size > playerCount) {
            error("Script was generated with too many roles!?")
        }

        if (getRolesInScript(script) != getPrimaryRolesInScript(script)) {
            error("There are roles in the script without sections where they are the primary role!?")
        }

        script.add(getAvailableSectionsForRoles(playerCount, SegmentTag.CLOSING, script, true).random())

        return script
    }

    // todo its possible to have a section that only mentions a role and that role ends up not having any speaking parts
    private fun getAvailableSectionsForRoles(playerCount: Int,
                                             filterByTag: SegmentTag,
                                             scriptSoFar: List<ScriptSection>,
                                             canAddNothing: Boolean = false): List<ScriptSection> {
        val primaryRolesInScript = getPrimaryRolesInScript(scriptSoFar)
        return ScriptParsingService.sections
            // if it is the type we are looking for
            .filter { it.tags.contains(filterByTag) }

            // and it adds at least one role
            // AND the new roles it would add to the script would still be less than our player count
            .filter {
                val newPrimaryRoleCount = (it.primaryRoles - primaryRolesInScript).size
                val newNonPrimaryRoles = it.primaryRoles - it.rolesInSection

                // this section adds something, or is allowed to add nothing
                return@filter (newPrimaryRoleCount > 0 || canAddNothing) &&
                        // and it wont put us over cap
                        newPrimaryRoleCount + primaryRolesInScript.size <= playerCount &&
                        // and all the non-primary roles already have a primary part
                        primaryRolesInScript.containsAll(newNonPrimaryRoles)
            }
    }

    fun getRolesInScript(script: List<ScriptSection>): Set<TIPURole> {
        return script.flatMap { it.rolesInSection }.toSet()
    }

    fun getPrimaryRolesInScript(script: List<ScriptSection>): Set<TIPURole> {
        return script.flatMap { it.primaryRoles }.toSet()
    }
}