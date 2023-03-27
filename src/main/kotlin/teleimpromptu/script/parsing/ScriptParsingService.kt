package teleimpromptu.script.parsing

import jsonDecoder
import kotlinx.serialization.decodeFromString

object ScriptParsingService
{
    // dont look its horrible...
    val sections: List<ScriptSection> = jsonDecoder.decodeFromString(
        this::class.java.classLoader.getResource("script/test.json")!!
            .readText())!!
}