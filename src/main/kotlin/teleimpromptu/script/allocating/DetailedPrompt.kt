package teleimpromptu.script.allocating

import teleimpromptu.TIPURole

class DetailedPrompt(val prompt: Prompt, val speakers: List<TIPURole>, val dependentPrompts: List<Prompt>)
