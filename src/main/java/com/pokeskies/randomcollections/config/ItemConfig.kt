package com.pokeskies.randomcollections.config

import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

class ItemConfig(
    val item: String = "",
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: CompoundTag? = null
) {
    fun getItemStack(player: ServerPlayer): ItemStack {
        if (item.isEmpty()) return ItemStack(Items.AIR, amount)

        val itemType = BuiltInRegistries.ITEM.get(ResourceLocation.parse(item))

        if (BuiltInRegistries.ITEM.defaultKey == BuiltInRegistries.ITEM.getKey(itemType)) {
            Utils.printError("Error while getting Item, defaulting to AIR: $item")
            return ItemStack(Items.AIR, amount)
        }

        val stack = ItemStack(itemType, amount)

        if (nbt != null) {
            val parsedNBT = nbt.copy()
            for (key in parsedNBT.allKeys) {
                val element = parsedNBT.get(key)
                if (element != null) {
                    if (element is StringTag) {
                        parsedNBT.putString(key, Utils.parsePlaceholders(player, element.asString))
                    } else if (element is ListTag) {
                        val parsed = ListTag()
                        for (entry in element) {
                            if (entry is StringTag) {
                                parsed.add(StringTag.valueOf(Utils.parsePlaceholders(player, entry.asString)))
                            } else {
                                parsed.add(entry)
                            }
                        }
                        parsedNBT.put(key, parsed)
                    }
                }
            }

            val newComponents = DataComponentPatch.CODEC.decode(RandomCollections.INSTANCE.nbtOpts, parsedNBT).result()
            if (newComponents.isPresent) {
                stack.applyComponents(newComponents.get().first)
            }
        }

        val dataComponents = DataComponentPatch.builder()

        if (name != null) {
            dataComponents.set(DataComponents.ITEM_NAME, Utils.deserializeText(Utils.parsePlaceholders(player, name)))
        }

        if (lore.isNotEmpty()) {
            val parsedLore: MutableList<String> = mutableListOf()
            for (line in lore) {
                if (line.contains("\n")) {
                    line.split("\n").forEach { _ ->
                        parsedLore.add(Utils.parsePlaceholders(player, line))
                    }
                } else {
                    parsedLore.add(Utils.parsePlaceholders(player, line))
                }
            }
            dataComponents.set(DataComponents.LORE, ItemLore(parsedLore.stream().map { Utils.deserializeText(it) }.toList()))
        }

        stack.applyComponents(dataComponents.build())

        return stack
    }
}
