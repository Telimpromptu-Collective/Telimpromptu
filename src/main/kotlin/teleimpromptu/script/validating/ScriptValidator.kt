package teleimpromptu.script.validating

import teleimpromptu.script.parsing.ScriptSection

typealias ScriptValidationError = String

interface ScriptValidator {
    fun validate(section: ScriptSection): List<ScriptValidationError>
}