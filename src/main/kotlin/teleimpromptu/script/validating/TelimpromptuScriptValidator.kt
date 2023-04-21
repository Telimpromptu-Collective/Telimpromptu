package teleimpromptu.script.validating

class TelimpromptuScriptValidator: AggregateScriptValidator(
    listOf(
        ReferencedPromptExistsValidator(),
        RoleNameValidator(),
        PromptsUsedValidator(),
        UniquePromptIdValidator(),
        FulfillablePromptValidator(),
    )
)