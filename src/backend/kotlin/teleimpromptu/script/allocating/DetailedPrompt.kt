package teleimpromptu.script.allocating

import teleimpromptu.TIPURole

class DetailedPrompt(val prompt: Prompt, public var shouldNotBeGivenTo: MutableList<TIPURole>, val dependentPrompts: List<Prompt>)
