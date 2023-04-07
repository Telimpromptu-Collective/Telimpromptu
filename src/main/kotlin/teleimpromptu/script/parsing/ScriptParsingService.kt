package teleimpromptu.script.parsing

import jsonDecoder
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import teleimpromptu.TIPURole
import java.io.File
import java.lang.Exception
import java.nio.file.Files

object ScriptParsingService
{
    val sections: List<ScriptSection> = parseScript()

    private fun parseScript(): List<ScriptSection> {
        val rawSections: List<RawScriptSection> = File(this::class.java.classLoader.getResource("script/production")!!.path).walk()
            .filter { it.extension == "json" }
            .flatMap {
                try {
                    jsonDecoder.decodeFromString<List<RawScriptSection>>(it.readText())
                } catch (ex: Exception) {
                    error("exception encountered while parsing ${it.name}:\n${ex.message}")
                }
            }
            .toList()

        // todo make this cleaner
        return rawSections.map { rawSection ->
            // all the roles that say stuff
            val rolesInSection: MutableList<TIPURole> = rawSection.lines.map { it.speaker }.distinct().toMutableList()

            // all the roles that get mentioned
            for (line in rawSection.lines) {
                for (role in TIPURole.values()) {
                    if (line.text.contains("{@${role.toLowercaseString()}}")) {
                        rolesInSection.add(role)
                    }
                }
            }

            return@map ScriptSection(
                rawSection.tags,
                rawSection.primaryRoles,
                rawSection.lines,
                rawSection.prompts,
                rolesInSection.distinct().toList())
        }
    }
}