package com.pokeskies.randomcollections.utils

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.config.ConfigManager
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.lang.reflect.Type
import java.util.*
import java.util.function.Function

object Utils {
    val miniMessage: MiniMessage = MiniMessage.miniMessage()

    fun parsePlaceholders(player: ServerPlayer, text: String): String {
        return RandomCollections.INSTANCE.placeholderManager.parse(player, text)
    }

    fun deserializeText(text: String): Component {
        return RandomCollections.INSTANCE.adventure.toNative(miniMessage.deserialize(text))
    }

    fun printDebug(message: String?, bypassCheck: Boolean = false) {
        if (bypassCheck || ConfigManager.CONFIG.debug)
            RandomCollections.LOGGER.info("[RandomCollections] DEBUG: $message")
    }

    fun printError(message: String?) {
        RandomCollections.LOGGER.error("[RandomCollections] ERROR: $message")
    }

    fun printInfo(message: String?) {
        RandomCollections.LOGGER.info("[RandomCollections] $message")
    }

    // Thank you to Patbox for these wonderful serializers =)
    data class RegistrySerializer<T>(val registry: Registry<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
            var parsed = if (json.isJsonPrimitive) registry.get(ResourceLocation.tryParse(json.asString)) else null
            if (parsed == null)
                printError("There was an error while deserializing a Registry Type: $registry")
            return parsed
        }
        override fun serialize(src: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(registry.getId(src).toString())
        }
    }

    data class CodecSerializer<T>(val codec: Codec<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T? {
            return try {
                codec.decode(JsonOps.INSTANCE, json).getOrThrow(false) { }.first
            } catch (e: Throwable) {
                printError("There was an error while deserializing a Codec: $codec")
                null
            }
        }

        override fun serialize(src: T?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return try {
                if (src != null)
                    codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false) { }
                else
                    JsonNull.INSTANCE
            } catch (e: Throwable) {
                printError("There was an error while serializing a Codec: $codec")
                JsonNull.INSTANCE
            }
        }
    }

    class ResourceLocationSerializer: JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ResourceLocation? {
            return ResourceLocation.tryParse(json.asString)
        }
        override fun serialize(src: ResourceLocation, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.asString())
        }
    }
}

fun <A, B> Codec<A>.recordCodec(id: String, getter: Function<B, A>): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).forGetter(getter)
}

fun <A, B> Codec<A>.optionalRecordCodec(id: String, getter: Function<B, A>, default: A): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).orElse(default).forGetter(getter)
}