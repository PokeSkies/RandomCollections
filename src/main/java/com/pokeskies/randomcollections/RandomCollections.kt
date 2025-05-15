package com.pokeskies.randomcollections

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.pokeskies.randomcollections.commands.BaseCommand
import com.pokeskies.randomcollections.config.ConfigManager
import com.pokeskies.randomcollections.config.rewards.Reward
import com.pokeskies.randomcollections.config.rewards.RewardType
import com.pokeskies.randomcollections.placeholders.PlaceholderManager
import com.pokeskies.randomcollections.utils.RandomCollection
import com.pokeskies.randomcollections.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files

class RandomCollections : ModInitializer {
    companion object {
        lateinit var INSTANCE: RandomCollections

        const val MOD_ID = "randomcollections"
        const val MOD_NAME = "RandomCollections"

        val LOGGER = LogManager.getLogger(MOD_ID)

        var COBBLEMON_PRESENT: Boolean = false
    }

    lateinit var configDir: File

    lateinit var placeholderManager: PlaceholderManager

    lateinit var adventure: FabricServerAudiences
    lateinit var server: MinecraftServer
    lateinit var nbtOpts: RegistryOps<Tag>

    var gson: Gson = GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(Reward::class.java, RewardType.RewardTypeAdaptor())
        .registerTypeHierarchyAdapter(CompoundTag::class.java, Utils.CodecSerializer(CompoundTag.CODEC))
        .registerTypeHierarchyAdapter(DataComponentMap::class.java, Utils.CodecSerializer(DataComponentMap.CODEC))
        .registerTypeHierarchyAdapter(DataComponentPatch::class.java, Utils.CodecSerializer(DataComponentPatch.CODEC))
        .create()

    var gsonPretty: Gson = gson.newBuilder().setPrettyPrinting().create()

    var collections: MutableMap<String, RandomCollection<Reward>> = mutableMapOf()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "randomcollections")
        ConfigManager.load()
        loadCollections()

        this.placeholderManager = PlaceholderManager()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server ->
            this.adventure = FabricServerAudiences.of(
                server
            )
            this.server = server
            this.nbtOpts = server.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        })
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { _ ->
            if (FabricLoader.getInstance().isModLoaded("cobblemon")) {
                COBBLEMON_PRESENT = true
            }
        })
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BaseCommand().register(
                dispatcher
            )
        }
    }

    fun reload() {
        ConfigManager.load()
        loadCollections()
    }

    private fun loadCollections() {
        collections.clear()
        ConfigManager.CONFIG.collections.forEach { (key, rewards) ->
            val rc = RandomCollection<Reward>()
            rewards.forEach { rc.add(it.weight, it) }
            collections[key] = rc
        }
    }

    fun <T : Any> loadFile(filename: String, default: T, create: Boolean = false): T {
        val file = File(configDir, filename)
        var value: T = default
        try {
            Files.createDirectories(configDir.toPath())
            if (file.exists()) {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = gsonPretty.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(gsonPretty.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: Exception) {
            Utils.printError("An error has occured while attempting to load file '$filename', with stacktrace:}")
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T): Boolean {
        val file = File(configDir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(gsonPretty.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
