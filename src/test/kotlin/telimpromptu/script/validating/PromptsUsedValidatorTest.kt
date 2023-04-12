package telimpromptu.script.validating

import org.junit.jupiter.api.Test
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.PromptGroup
import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SinglePrompt
import teleimpromptu.script.validating.PromptsUsedValidator

class PromptsUsedValidatorTest {
    @Test
    fun `empty section no errors`() {
        val section = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = emptyList(),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = PromptsUsedValidator()
        val result = validator.validate(section)
        assert(result.isEmpty())
    }

    @Test
    fun `test valid prompt parsing returns no errors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = listOf(
                ScriptLine(
                    speaker = TIPURole.COHOST,
                    text = "that's right {!dummy} and {!dooming}",
                ),
            ),
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
        val validator = PromptsUsedValidator()
        val result = validator.validate(validSection)
        assert(result.isEmpty())
    }

    @Test
    fun `test prompts not used return ScriptValidationErrors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = listOf(
                ScriptLine(
                    speaker = TIPURole.COHOST,
                    text = "that's right!",
                ),
            ),
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
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = PromptsUsedValidator()
        val result = validator.validate(validSection)
        // There are two prompts that are unused in the section lines
        assert(result.size == 2)
    }
}