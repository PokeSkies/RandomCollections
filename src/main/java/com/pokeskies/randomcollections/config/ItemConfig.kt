package com.pokeskies.randomcollections.config

import com.pokeskies.randomcollections.utils.Utils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ItemConfig(
    val item: String = "",
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: CompoundTag? = null
) {
    fun getItemStack(player: ServerPlayer): ItemStack {
        if (item.isEmpty()) return ItemStack(Items.AIR, amount)

        val itemType = BuiltInRegistries.ITEM.get(ResourceLocation(item))

        if (BuiltInRegistries.ITEM.defaultKey == BuiltInRegistries.ITEM.getKey(itemType)) {
            Utils.printError("Error while getting Item, defaulting to AIR: $item")
            return ItemStack(Items.AIR, amount)
        }

        val stack = ItemStack(itemType, amount)

        if (nbt != null) {
            val copyNBT = nbt.copy()
            if (stack.tag != null && !stack.tag!!.isEmpty) {
                for (key in copyNBT.allKeys) {
                    stack.tag!!.put(key, copyNBT.get(key)!!)
                }
            } else {
                stack.tag = copyNBT
            }
        }

        if (name != null) {
            stack.setHoverName(Utils.deserializeText(Utils.parsePlaceholders(player, name)))
        }

        if (lore.isNotEmpty()) {
            val nbtLore = ListTag()
            for (line in lore) {
                if (line.contains("\n")) {
                    line.split("\n").forEach { _ ->
                        nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(
                            Utils.deserializeText(Utils.parsePlaceholders(player, line))
                        )))
                    }
                } else {
                    nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(
                        Utils.deserializeText(Utils.parsePlaceholders(player, line))
                    )))
                }
            }
            stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY).put("Lore", nbtLore)
        }

        return stack
    }
}