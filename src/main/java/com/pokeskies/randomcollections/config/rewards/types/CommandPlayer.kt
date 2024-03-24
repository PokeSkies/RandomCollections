package com.pokeskies.randomcollections.config.rewards.types

import com.google.gson.annotations.JsonAdapter
import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.config.rewards.Reward
import com.pokeskies.randomcollections.config.rewards.RewardType
import com.pokeskies.randomcollections.utils.FlexibleListAdaptorFactory
import com.pokeskies.randomcollections.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CommandPlayer(
    type: RewardType = RewardType.COMMAND_PLAYER,
    weight: Double,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val commands: List<String>
) : Reward(type, weight) {
    override fun giveReward(player: ServerPlayer) {
        RandomCollections.INSTANCE.server.let { server ->
            commands.forEach { command ->
                server.commands.performPrefixedCommand(player.createCommandSourceStack(), Utils.parsePlaceholders(player, command))
            }
        }
    }
}