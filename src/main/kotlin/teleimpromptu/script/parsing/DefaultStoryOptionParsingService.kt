package teleimpromptu.script.parsing

import jsonDecoder
import kotlinx.serialization.decodeFromString
import teleimpromptu.states.storySelection.TIPUStory

object DefaultStoryOptionParsingService {
    val defaultStoryOptions: List<TIPUStory> = parseDefaultStoryOptions()
    private fun parseDefaultStoryOptions(): List<TIPUStory> {
        val fileContent = this::class.java.classLoader.getResource("defaultStories.json")!!.readText()
        val storyStrings = jsonDecoder.decodeFromString<List<String>>(fileContent)
        return storyStrings.map { TIPUStory(null, it) }
    }
}