package telimpromptu.script.validating

import org.junit.jupiter.api.Test
import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.PromptGroup
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SinglePrompt
import teleimpromptu.script.validating.FulfillablePromptValidator

class FulfillablePromptValidatorTest {
    @Test
    fun `empty section no errors`() {
        val section = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = emptyList(),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = FulfillablePromptValidator()
        val result = validator.validate(section)
        assert(result.isEmpty())
    }

    @Test
    fun `test valid prompt parsing returns no errors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = listOf(
                PromptGroup(
                    groupId = "whatever",
                    subPrompts = listOf(
                        SinglePrompt(
                            id = "dummy",
                            description = "whatever {!dooming}!",
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
        val validator = FulfillablePromptValidator()
        val result = validator.validate(validSection)
        assert(result.isEmpty())
    }

    @Test
    fun `basic circular dependency returns ScriptValidationErrors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = listOf(
                SinglePrompt(
                    id = "prompt1",
                    description = "I need {!prompt2} :(",
                ),
                SinglePrompt(
                    id = "prompt2",
                    description = "I need {!prompt1} :(",
                ),
            ),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = FulfillablePromptValidator()
        val result = validator.validate(validSection)
        // prompt 1 depends on prompt 2 and vice-versa, circular dependency
        assert(result.isNotEmpty())
    }

    @Test
    fun `three-way circular dependency returns ScriptValidationErrors`() {
        val validSection = ScriptSection(
            tags = emptyList(),
            lines = emptyList(),
            prompts = listOf(
                SinglePrompt(
                    id = "prompt1",
                    description = "I need {!prompt2} :(",
                ),
                SinglePrompt(
                    id = "prompt2",
                    description = "I need {!prompt3} :(",
                ),
                SinglePrompt(
                    id = "prompt3",
                    description = "I need {!prompt1} :(",
                ),
            ),
            rolesInSection = emptyList(),
            primaryRoles = emptyList(),
        )
        val validator = FulfillablePromptValidator()
        val result = validator.validate(validSection)
        // prompt1 requires prompt2, prompt 2 requires prompt3, prompt3 requires prompt1, circular dependency
        assert(result.isNotEmpty())
    }
}