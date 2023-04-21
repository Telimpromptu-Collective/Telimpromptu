package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection

class PromptsUsedValidator: ScriptValidator {
    override fun validate(section: ScriptSection): List<ScriptValidationError> {
        val out = mutableListOf<ScriptValidationError>()
        val referencedPromptIds = section.lines.map { getPromptIdStringsInLine(it) }.flatten().toSet()
        val sectionPromptIds = section.prompts.map { prompt -> prompt.unpack().map { it.id } }.flatten()
        for (promptId in sectionPromptIds) {
            if (!referencedPromptIds.contains(promptId)) {
                out.add("Found unused prompt ID $promptId")
            }
        }
        return out
    }

    /**
     * Finds all strings that match the pattern {!<STRING>}, and returns the values of contained Strings, trimming off
     * the preceding {! and following } characters.
     */
    private fun getPromptIdStringsInLine(line: ScriptLine): List<String> {
        val regex = """\{![^}]+}""".toRegex()

        val matches = regex.findAll(line.text).map { it.value }
        return matches
            .map { it.subSequence(2, it.length - 1).toString() }
            .toList()
    }
}