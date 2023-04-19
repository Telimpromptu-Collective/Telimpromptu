package teleimpromptu.states.promptAnswering

import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsMessageContext
import jsonDecoder
import kotlinx.serialization.encodeToString
import teleimpromptu.*
import teleimpromptu.message.*
import teleimpromptu.script.allocating.PromptAllocator
import teleimpromptu.script.building.ScriptBuilderService
import teleimpromptu.script.formatting.PromptFormatter
import teleimpromptu.script.parsing.ScriptLine
import teleimpromptu.script.parsing.ScriptSection
import teleimpromptu.script.parsing.SinglePrompt
import teleimpromptu.states.storySelection.TIPUStorySelectionPlayer
import kotlin.random.Random

class TIPUPromptAnsweringState(players: List<TIPUStorySelectionPlayer>,
                               private val storyOfTheNight: String,
                               private val tipuSession: TIPUSession): TIPUSessionState {

    private val lastNameMap = mapOf(
        TIPURole.HOST to listOf("Newsly", "Hostman", "Hostdanews"),
        TIPURole.COHOST to listOf("McNewsman", "Newsperson", "Hosterson"),
        TIPURole.GUESTEXPERT to listOf("Expertson", "Knowsalot", "McQualified"),
        TIPURole.DETECTIVE to listOf("Gumshoe", "McSniff", "Sleuthburger"),
        TIPURole.FIELDREPORTER to listOf("Reportson", "McReporter", "Rerpotsalot"),
        TIPURole.WITNESS to listOf("Realman", "Eyeball"),
        TIPURole.COMMENTATOR to listOf("Smith"),
        TIPURole.ZOOKEEPER to listOf("Zooman", "King", "Animalman"),
        TIPURole.RELIGIOUSLEADER to listOf("Smith")
    )

    private val players: List<TIPUPromptAnsweringPlayer>
    private val script: List<ScriptSection> = ScriptBuilderService.buildScriptForPlayerCount(players.size)

    private val promptAllocator: PromptAllocator
    private val promptFormatter: PromptFormatter

    init {
        val roles = ScriptBuilderService.getPrimaryRolesInScript(script)

        val playersWithRoles = (players.shuffled() zip roles)
            .map { (entry, role) ->
                val lastNameList = lastNameMap[role] ?: error("No last name for $role")
                val lastName = lastNameList[Random.nextInt(lastNameList.size)]
                TIPUPromptAnsweringPlayer(entry.username, role, lastName, entry.connection)
            }

        this.players = playersWithRoles

        this.promptAllocator = PromptAllocator(this.players, script)
        this.promptFormatter = PromptFormatter(this.players)


        val json = jsonDecoder
            .encodeToString(EnterPromptAnsweringStateMessage(storyOfTheNight,
                this.players.map { IngamePlayerStatus(it.username, it.role.toLowercaseString()) }))

        this.players.forEach {
            it.connection.send(json)
        }

        // send everyone their prompts
        promptAllocator.allocateAvailablePrompts().forEach { sendPromptsToPlayer(it.key, it.value) }
    }
    override fun receiveMessage(ctx: WsMessageContext, message: Message) {
        // find the sender player, return if its not someone we know
        val sender = players.firstOrNull { it.connection == ctx } ?: return

        when (message) {
            is PromptResponseMessage -> {
                println("recieved: " + message.id + ": " + message.response)

                // todo trusting this id allows hackerz!
                promptFormatter.addPromptResponse(message.id, message.response)

                // get the prompts and send the prompts to everyone
                promptAllocator.moveCompletedPromptsAndAllocate(listOf(message.id), sender)
                    .forEach { sendPromptsToPlayer(it.key, it.value) }

                if (promptAllocator.areAllPromptsComplete()) {
                    players.forEach { it.connection.send(jsonDecoder.encodeToString(PromptsCompleteMessage())) }
                }
            }
            else -> println("fail game: $message")
        }
    }

    override fun recieveConnectionMessage(ctx: WsMessageContext, message: UserConnectMessage) {
        // if this user is connecting with a username not in the lobby, stop
        val reconnectingUser = players.find { it.username == message.username } ?: return

        // if someone already connected with this username kick them and set this as the new one lol
        reconnectingUser.connection.closeSession()
        reconnectingUser.connection = ctx

        // send them the start game info to get them up to speed
        val json = jsonDecoder
            .encodeToString(EnterPromptAnsweringStateMessage(storyOfTheNight,
                players.map { IngamePlayerStatus(it.username, it.role.toLowercaseString()) }))
        reconnectingUser.connection.send(json)

        // send them their incomplete prompts
        sendPromptsToPlayer(reconnectingUser, promptAllocator.outstandingPromptsForPlayer(reconnectingUser))
    }

    override fun receiveDisconnect(ctx: WsCloseContext) {
        println("connection closed....")
    }

    // todo maybe this should be unpacked beforehand since reconnecting has to be
    private fun sendPromptsToPlayer(player: TIPUPromptAnsweringPlayer, prompts: List<SinglePrompt>) {
        // format script prompts
        val formattedScriptPrompts = prompts.map { SinglePrompt(it.id, promptFormatter.formatText(it.description)) }

        player.connection.send(jsonDecoder.encodeToString(NewPromptsMessage(formattedScriptPrompts)))
    }

    fun getFullFormattedScript(): List<ScriptLine> {
        return script.flatMap { it.lines }.map { ScriptLine(it.speaker, promptFormatter.formatText(it.text)) }
    }

    fun getPlayers(): List<TIPUPromptAnsweringPlayer> {
        return players
    }
}