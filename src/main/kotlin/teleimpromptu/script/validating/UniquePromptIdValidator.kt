package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptSection

class UniquePromptIdValidator: ScriptValidator {
    override fun validate(section: ScriptSection): List<ScriptValidationError> {
        val sectionPrompts = section.prompts.map { it.unpack() }.flatten()
        val groupedPrompts = mutableMapOf<String, MutableList<String>>()
        for (prompt in sectionPrompts) {
            if (!groupedPrompts.containsKey(prompt.id)) {
                groupedPrompts[prompt.id] = mutableListOf()
            }
            groupedPrompts[prompt.id]!!.add(prompt.description)
        }

        return groupedPrompts.filter { it.value.size > 1 }.map { "Prompt ${it.key} is used as the ID for multiple prompts. Descriptions: \n\t${it.value.joinToString { "\n\t" }}" }
    }
}