package com.pokeskies.randomcollections.config.rewards.types

import com.google.gson.annotations.JsonAdapter
import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.config.rewards.Reward
import com.pokeskies.randomcollections.config.rewards.RewardType
import com.pokeskies.randomcollections.utils.FlexibleListAdaptorFactory
import com.pokeskies.randomcollections.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CommandConsole(
    type: RewardType = RewardType.COMMAND_CONSOLE,
    weight: Double,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val commands: List<String>
) : Reward(type, weight) {
    override fun giveReward(player: ServerPlayer) {
        RandomCollections.INSTANCE.server.let { server ->
            val parsed = commands.map { Utils.parsePlaceholders(player, it) }
            Utils.printDebug("[REWARD - ${type.name}] Player(${player.gameProfile.name}), Commands($parsed)")
            parsed.forEach { command ->
                server.commands.performPrefixedCommand(server.createCommandSourceStack(), command)
            }
        }
    }
}