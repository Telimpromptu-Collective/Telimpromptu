package teleimpromptu

enum class TIPURole {
    HOST, COHOST, GUESTEXPERT, DETECTIVE, FIELDREPORTER, WITNESS, COMMENTATOR, ZOOKEEPER, RELIGIOUSLEADER;

    fun toLowercaseString(): String {
        return when(this) {
            HOST -> "host"
            COHOST -> "cohost"
            GUESTEXPERT -> "guestexpert"
            DETECTIVE -> "detective"
            FIELDREPORTER -> "fieldreporter"
            WITNESS -> "witness"
            COMMENTATOR -> "commentator"
            ZOOKEEPER -> "zookeeper"
            RELIGIOUSLEADER -> "religiousleader"
        }
    }
}