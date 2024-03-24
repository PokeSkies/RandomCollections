package com.pokeskies.randomcollections.placeholders.services

import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.utils.Utils
import com.pokeskies.randomcollections.placeholders.IPlaceholderService
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.minecraft.server.level.ServerPlayer

class MiniPlaceholdersService : IPlaceholderService {
    private val miniMessage = MiniMessage.builder()
        .tags(TagResolver.builder().build())
        .build()

    init {
        Utils.printInfo("MiniPlaceholders mod found! Enabling placeholder integration...")
    }

    override fun parsePlaceholders(player: ServerPlayer, text: String): String {
        val resolver = TagResolver.resolver(
            MiniPlaceholders.getGlobalPlaceholders(),
            MiniPlaceholders.getAudiencePlaceholders(player)
        )

        return RandomCollections.INSTANCE.adventure.toNative(
            miniMessage.deserialize(text, resolver)
        ).string
    }
}