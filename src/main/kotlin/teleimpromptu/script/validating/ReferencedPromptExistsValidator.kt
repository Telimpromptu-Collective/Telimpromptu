package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection

class ReferencedPromptExistsValidator: ScriptValidator {
    override fun validate(section: ScriptSection): List<ScriptValidationError> {
        val out = mutableListOf<ScriptValidationError>()
        // these prompts are always valid.
        val defaultPrompts = listOf("main_story")
        val sectionPrompts = defaultPrompts +
                section.prompts.map { prompt -> prompt.unpack().map { it.id } }.flatten()
                    .toSet()
        for (line in section.lines) {
            val referencedPrompts = getPromptIdStringsInLine(line).distinct()
            val missingPrompts = referencedPrompts.filterNot { sectionPrompts.contains(it) }
            out.addAll(missingPrompts.map { "Prompt [$it] is referenced but not in ScriptSection in line: ${line.text}" })
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