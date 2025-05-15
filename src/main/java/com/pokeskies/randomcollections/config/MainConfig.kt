package com.pokeskies.randomcollections.config

import com.pokeskies.randomcollections.config.rewards.Reward

class MainConfig(
    var debug: Boolean = false,
    var collections: MutableMap<String, List<Reward>> = mutableMapOf()
) {
    override fun toString(): String {
        return "MainConfig(debug=$debug, collections=$collections)"
    }

    class Collection(
        val rewards: List<Reward> = emptyList()
    ) {
        override fun toString(): String {
            return "Collection(rewards=$rewards)"
        }
    }
}
