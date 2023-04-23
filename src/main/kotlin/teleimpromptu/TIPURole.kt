package teleimpromptu

import kotlin.random.Random

enum class TIPURole(val lastNames: List<String>) {
    HOST(listOf("Newsly", "Hostman", "Hostdanews")),
    COHOST(listOf("McNewsman", "Newsperson", "Hosterson")),
    GUESTEXPERT(listOf("Expertson", "Knowsalot", "McQualified")),
    DETECTIVE(listOf("Gumshoe", "McSniff", "Sleuthburger")),
    FIELDREPORTER(listOf("Reportson", "McReporter", "Rerportsalot")),
    WITNESS(listOf("Realman")),
    COMMENTATOR(listOf("Smith")),
    ZOOKEEPER(listOf("Zooman", "King", "Animalman")),
    RELIGIOUSLEADER(listOf("Smith")),
    POLITICALREPORTER(listOf("")),
    SPORTSREPORTER(listOf());

    // maybe we dont need this :p
    fun toLowercaseString(): String {
        return this.toString().lowercase()
    }

    fun randomLastName(): String {
        return lastNames[Random.nextInt(lastNames.size)]
    }
}