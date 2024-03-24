package com.pokeskies.randomcollections.placeholders.services

import com.pokeskies.randomcollections.placeholders.IPlaceholderService
import net.minecraft.server.level.ServerPlayer

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayer, text: String): String {
        return text.replace("%player%", player.name.string)
    }
}