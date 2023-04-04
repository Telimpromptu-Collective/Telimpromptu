package teleimpromptu.script.allocating

import teleimpromptu.TIPURole

class DetailedPrompt(val prompt: Prompt, val shouldNotBeGivenTo: List<TIPURole>, val dependentPrompts: List<Prompt>)
