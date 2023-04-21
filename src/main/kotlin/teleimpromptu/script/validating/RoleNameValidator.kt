package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection

class RoleNameValidator: ScriptValidator {
    override fun validate(section: ScriptSection): List<ScriptValidationError> {
        val out = mutableListOf<ScriptValidationError>()
        val sectionRoleTags = section.rolesInSection.map { it.toLowercaseString() }.toSet()
        for (line in section.lines) {
            val referencedRoles = getReferencedRoleStringsInLine(line).distinct()
            val missingRoles = referencedRoles.filterNot { sectionRoleTags.contains(it) }
            out.addAll(missingRoles.map { "Role [$it] is referenced but not in ScriptSection in line: ${line.text}" })
        }
        return out
    }

    /**
     * Finds all strings that match the pattern {@<STRING>}, and returns the values of contained Strings, trimming off
     * the preceding {@ and following } characters.
     */
    private fun getReferencedRoleStringsInLine(line: ScriptLine): List<String> {
        val regex = """\{@(?!.*_lastname)[^}]+}""".toRegex()

        val matches = regex.findAll(line.text).map { it.value }
        return matches
            .map { it.subSequence(2, it.length - 1).toString() }
            .toList()
    }
}