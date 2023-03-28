package manualtesting

import teleimpromptu.script.building.ScriptBuilderService

fun main() {
    val s = ScriptBuilderService.buildScriptForPlayerCount(5)
    s.forEach { it.lines.forEach { println(it.speaker.toString() + ": " + it.text) }}
}