package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptSection

open class AggregateScriptValidator(
    private val scriptValidators: List<ScriptValidator>
): ScriptValidator {
    override fun validate(section: ScriptSection): List<ScriptValidationError> {
        return scriptValidators
            .map { it.validate(section) }
            .flatten()
    }
}