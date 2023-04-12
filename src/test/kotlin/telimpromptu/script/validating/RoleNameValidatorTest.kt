package telimpromptu.script.validating

import org.junit.jupiter.api.Test
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.validating.RoleNameValidator

class RoleNameValidatorTest {
    @Test
    fun `empty section no errors`() {
        val section = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = emptyList(),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = RoleNameValidator()
        val result = validator.validate(section)
        assert(result.isEmpty())
    }

    @Test
    fun `test valid role parsing returns no errors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = listOf(
                ScriptLine(
                    speaker = TIPURole.COHOST,
                    text = "that's right {@${TIPURole.COMMENTATOR.toLowercaseString()}} and {@${TIPURole.FIELDREPORTER.toLowercaseString()}}"
                ),
            ),
            prompts = emptyList(),
            rolesInSection = listOf(TIPURole.COMMENTATOR, TIPURole.FIELDREPORTER),
            primaryRoles = emptyList(),
        )
        val validator = RoleNameValidator()
        val result = validator.validate(validSection)
        assert(result.isEmpty())
    }

    @Test
    fun `test roles not in role section return ScriptValidationErrors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = listOf(
                ScriptLine(
                    speaker = TIPURole.COHOST,
                    text = "that's right {@${TIPURole.COMMENTATOR.toLowercaseString()}} and {@${TIPURole.FIELDREPORTER.toLowercaseString()}}"
                ),
            ),
            prompts = emptyList(),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = RoleNameValidator()
        val result = validator.validate(validSection)
        // There are two roles referenced in the script text that are not in the section
        assert(result.size == 2)
    }
}