package com.pokeskies.randomcollections.config.rewards.types

import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.config.ItemConfig
import com.pokeskies.randomcollections.config.rewards.Reward
import com.pokeskies.randomcollections.config.rewards.RewardType
import net.minecraft.server.level.ServerPlayer

class Item(
    type: RewardType = RewardType.ITEM,
    weight: Double,
    val item: ItemConfig
) : Reward(type, weight) {
    override fun giveReward(player: ServerPlayer) {
        player.inventory.add(item.getItemStack(player))
    }
}