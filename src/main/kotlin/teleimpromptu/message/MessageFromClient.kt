package teleimpromptu.message

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Polymorphic
@Serializable(with = MessageSerializer::class)
open class Message

@Serializable
data class CreateUserMessage(val type: String, val username: String): Message()

@Serializable
data class StartGameMessage(
    val type: String,
    val minionCount: Int
): Message()


object MessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(
        element: JsonElement
    ): DeserializationStrategy<out Message> {
        return when (val type = element.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
            "createUser" -> CreateUserMessage.serializer()
            "startGame" -> StartGameMessage.serializer()
            else -> error("unknown message type $type")
        }
    }
}