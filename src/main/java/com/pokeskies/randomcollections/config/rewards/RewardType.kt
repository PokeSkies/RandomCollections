package com.pokeskies.randomcollections.config.rewards

import com.google.gson.*
import com.pokeskies.randomcollections.config.rewards.types.CommandConsole
import com.pokeskies.randomcollections.config.rewards.types.CommandPlayer
import com.pokeskies.randomcollections.config.rewards.types.Item
import java.lang.reflect.Type

enum class RewardType(val identifier: String, val clazz: Class<*>) {
    COMMAND_CONSOLE("command_console", CommandConsole::class.java),
    COMMAND_PLAYER("command_player", CommandPlayer::class.java),
    ITEM("item", Item::class.java);

    companion object {
        fun valueOfAnyCase(name: String): RewardType? {
            for (type in values()) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class RewardTypeAdaptor : JsonSerializer<Reward>, JsonDeserializer<Reward> {
        override fun serialize(src: Reward, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src, src::class.java)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Reward {
            val jsonObject: JsonObject = json.getAsJsonObject()
            val value = jsonObject.get("type").asString
            val type: RewardType? = valueOfAnyCase(value)
            return try {
                context.deserialize(json, type!!.clazz)
            } catch (e: NullPointerException) {
                throw JsonParseException("Could not deserialize reward type: $value", e)
            }
        }
    }
}