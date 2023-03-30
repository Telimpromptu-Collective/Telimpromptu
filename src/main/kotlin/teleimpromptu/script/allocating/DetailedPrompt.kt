package teleimpromptu.script.allocating

import teleimpromptu.TIPURole

class DetailedPrompt(prompt: Prompt, val speakers: List<TIPURole>, val dependentPrompts: List<Prompt>)
