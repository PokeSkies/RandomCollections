package com.pokeskies.randomcollections.config

import com.pokeskies.randomcollections.config.rewards.Reward

class MainConfig(
    var debug: Boolean = false,
    var collections: Map<String, List<Reward>> = emptyMap()
) {
    override fun toString(): String {
        return "MainConfig(debug=$debug, collections=$collections)"
    }
}