package telimpromptu.script.validating

import org.junit.jupiter.api.Test
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.PromptGroup
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SinglePrompt
import teleimpromptu.script.validating.UniquePromptIdValidator

class UniquePromptIdValidatorTest {
    @Test
    fun `empty section no errors`() {
        val section = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = emptyList(),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = UniquePromptIdValidator()
        val result = validator.validate(section)
        assert(result.isEmpty())
    }

    @Test
    fun `test non-duplicate prompts returns no errors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = listOf(
                PromptGroup(
                    groupId = "whatever",
                    subPrompts = listOf(
                        SinglePrompt(
                            id = "dummy",
                            description = "whatever!",
                        ),
                    )
                ),
                SinglePrompt(
                    id = "dooming",
                    description = "whatever again!",
                ),
            ),
            rolesInSection = listOf(TIPURole.COMMENTATOR, TIPURole.FIELDREPORTER),
            primaryRoles = emptyList(),
        )
        val validator = UniquePromptIdValidator()
        val result = validator.validate(validSection)
        assert(result.isEmpty())
    }

    @Test
    fun `test duplicate IDs return ScriptValidationError`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = listOf(
                PromptGroup(
                    groupId = "whatever",
                    subPrompts = listOf(
                        SinglePrompt(
                            id = "dummy",
                            description = "whatever!",
                        ),
                    )
                ),
                SinglePrompt(
                    id = "dummy",
                    description = "whatever again!",
                ),
            ),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = UniquePromptIdValidator()
        val result = validator.validate(validSection)
        // There are two roles referenced in the script text that are not in the section
        assert(result.size == 1)
    }
}