package teleimpromptu.script.allocating

import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptPrompt

class DetailedScriptPrompt (val id: String, val description: String, val speakers: List<TIPURole>, //val dependencyPrompts: MutableList<ScriptPrompt>
)