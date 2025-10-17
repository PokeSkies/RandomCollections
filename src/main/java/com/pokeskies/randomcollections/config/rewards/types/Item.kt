package com.pokeskies.randomcollections.config.rewards.types

import com.pokeskies.randomcollections.config.ItemConfig
import com.pokeskies.randomcollections.config.rewards.Reward
import com.pokeskies.randomcollections.config.rewards.RewardType
import com.pokeskies.randomcollections.utils.Utils
import net.minecraft.server.level.ServerPlayer

class Item(
    type: RewardType = RewardType.ITEM,
    weight: Double,
    val item: ItemConfig
) : Reward(type, weight) {
    override fun giveReward(player: ServerPlayer) {
        Utils.printDebug("[REWARD - ${type.name}] Player(${player.gameProfile.name}), item($item)")
        player.inventory.add(item.getItemStack(player))
    }
}
