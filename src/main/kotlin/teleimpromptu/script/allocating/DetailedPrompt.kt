package teleimpromptu.script.allocating

import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptPrompt

class DetailedPrompt(val prompt: ScriptPrompt, public var shouldNotBeGivenTo: MutableList<TIPURole>, val dependentPrompts: List<ScriptPrompt>)
