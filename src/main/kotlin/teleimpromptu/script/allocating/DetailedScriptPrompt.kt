package teleimpromptu.script.allocating

import teleimpromptu.TIPURole
import teleimpromptu.script.parsing.ScriptPrompt

class DetailedScriptPrompt (scriptPrompt: ScriptPrompt, val speakers: MutableList<TIPURole>)