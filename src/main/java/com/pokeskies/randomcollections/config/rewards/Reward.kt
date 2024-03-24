package com.pokeskies.randomcollections.config.rewards

import net.minecraft.server.level.ServerPlayer

abstract class Reward(
    var type: RewardType,
    var weight: Double
) {
    abstract fun giveReward(player: ServerPlayer)

    override fun toString(): String {
        return "Reward(type=$type, weight=$weight)"
    }
}