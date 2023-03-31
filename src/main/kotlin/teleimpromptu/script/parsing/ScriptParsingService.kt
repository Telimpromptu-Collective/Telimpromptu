package teleimpromptu.script.parsing

import jsonDecoder
import kotlinx.serialization.decodeFromString
import teleimpromptu.TIPURole

object ScriptParsingService
{
    val sections: List<ScriptSection> = parseScript()

    private fun parseScript(): List<ScriptSection> {
        // yuck
        val rawSections: List<RawScriptSection> = jsonDecoder.decodeFromString(
            this::class.java.classLoader.getResource("script/test.json")!!
                .readText()
        )!!

        // todo make this cleaner
        return rawSections.map { rawSection ->
            // all the roles that say stuff
            val rolesInSection: MutableList<TIPURole> = rawSection.lines.map { it.speaker }.distinct().toMutableList()

            // all the roles that get mentioned
            for (line in rawSection.lines) {
                for (role in TIPURole.values()) {
                    if (line.text.contains("{@${role.toLowercaseString()}")) {
                        rolesInSection.add(role)
                    }
                }
            }

            return@map ScriptSection(rawSection.tags, rawSection.lines, rawSection.prompts, rolesInSection.toList())
        }
    }
}