package teleimpromptu.message

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Polymorphic
@Serializable(with = MessageSerializer::class)
open class Message

// todo maybe figure out how to not have to include type here it isnt needed
@Serializable
data class CreateUserMessage(val type: String, val username: String): Message()

@Serializable
data class StartGameMessage(
    val type: String
): Message()

@Serializable
data class PromptResponseMessage(
    val type: String,
    val response: String,
    val id: String
): Message()



object MessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(
        element: JsonElement
    ): DeserializationStrategy<out Message> {
        return when (val type = element.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
            "createUser" -> CreateUserMessage.serializer()
            "startGame" -> StartGameMessage.serializer()
            "promptResponse" -> PromptResponseMessage.serializer()
            else -> error("unknown message type $type")
        }
    }
}