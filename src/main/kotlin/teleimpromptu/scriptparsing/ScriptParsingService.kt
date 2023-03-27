package teleimpromptu.scriptparsing

import com.beust.klaxon.Klaxon
import java.io.File

object ScriptParsingService
{
    val sections: List<ScriptSection> =
        Klaxon().parseArray(this::class.java.classLoader.getResource("script/test.json")!!.readText())!!
}