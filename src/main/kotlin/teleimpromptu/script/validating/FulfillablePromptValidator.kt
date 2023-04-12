package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptSection

class FulfillablePromptValidator: ScriptValidator {
    override fun validate(section: ScriptSection): List<ScriptValidationError> {
        val validationErrors = mutableListOf<ScriptValidationError>()

        val dependencyMap = buildPromptDependencyMap(section)
        val visited = mutableSetOf<String>()
        val stack = mutableSetOf<String>()
        for (prompt in dependencyMap.keys) {
            if (isCircularDependency(id = prompt, dependencyMap = dependencyMap, visited = visited, stack = stack)) {
                validationErrors.add("Circular dependency found for prompt $prompt")
            }
        }

        return validationErrors
    }

    private fun isCircularDependency(
        id: String,
        dependencyMap: Map<String, List<String>>,
        visited: MutableSet<String>,
        stack: MutableSet<String>
    ): Boolean {
        if (stack.contains(id)) {
            return true
        }

        if (visited.contains(id)) {
            // seen before, but was not a circular dependency
            return false
        }

        visited.add(id)
        stack.add(id)

        for (dependency in dependencyMap[id] ?: emptySet()) {
            if (isCircularDependency(dependency, dependencyMap, visited, stack)) {
                return true
            }
        }

        // no circular dependency found!
        stack.remove(id)
        return false
    }

    private fun buildPromptDependencyMap(section: ScriptSection): Map<String, List<String>> {
        val dependencyMap = mutableMapOf<String, List<String>>()
        val allPrompts = section.prompts.map { it.unpack() }.flatten()
        for (prompt in allPrompts) {
            val dependencyPromptIds = getPromptIdStringsInText(prompt.description)
            dependencyMap[prompt.id] = dependencyPromptIds
        }
        return dependencyMap
    }

    /**
     * Finds all strings that match the pattern {!<STRING>}, and returns the values of contained Strings, trimming off
     * the preceding {! and following } characters.
     */
    private fun getPromptIdStringsInText(text: String): List<String> {
        val regex = """\{![^}]+}""".toRegex()

        val matches = regex.findAll(text).map { it.value }
        return matches
            .map { it.subSequence(2, it.length - 1).toString() }
            .toList()
    }
}