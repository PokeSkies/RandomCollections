package com.pokeskies.randomcollections.config

import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.pokeskies.randomcollections.RandomCollections
import com.pokeskies.randomcollections.utils.Utils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

object ConfigManager {
    private var assetPackage = "assets/${RandomCollections.MOD_ID}"

    lateinit var CONFIG: MainConfig

    fun load() {
        // Load defaulted configs if they do not exist
        copyDefaults()

        // Load all files
        CONFIG = loadFile("config.json", MainConfig())
        loadCollections()
    }

    private fun copyDefaults() {
        val classLoader = RandomCollections::class.java.classLoader

        RandomCollections.INSTANCE.configDir.mkdirs()

        attemptDefaultFileCopy(classLoader, "config.json")
        attemptDefaultDirectoryCopy(classLoader, "collections")
    }

    private fun loadCollections() {
        val dir = RandomCollections.INSTANCE.configDir.resolve("collections")
        if (dir.exists() && dir.isDirectory) {
            val files = Files.walk(dir.toPath())
                .filter { path: Path -> Files.isRegularFile(path) }
                .map { it.toFile() }
                .collect(Collectors.toList())
            if (files != null) {
                RandomCollections.LOGGER.info("Found ${files.size} collection files: ${files.map { it.name }}")
                val enabledFiles = mutableListOf<String>()
                for (file in files) {
                    val fileName = file.name
                    if (CONFIG.collections.containsKey(fileName)) {
                        Utils.printError("Collection file $fileName is already in the config, skipping...")
                        continue
                    }
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            val collection = RandomCollections.INSTANCE.gsonPretty.fromJson(JsonParser.parseReader(jsonReader), MainConfig.Collection::class.java)
                            CONFIG.collections[id] = collection.rewards
                            enabledFiles.add(fileName)
                        } catch (ex: Exception) {
                            Utils.printError("Error while trying to parse the collection file $fileName!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.printError("File $fileName is either not a file or is not a .json file!")
                    }
                }
                Utils.printInfo("Successfully read and loaded the following enabled collection files: $enabledFiles")
            }
        } else {
            Utils.printError("The 'collections' directory either does not exist or is not a directory!")
        }
    }

    fun <T : Any> loadFile(filename: String, default: T, path: String = "", create: Boolean = false): T {
        var dir = RandomCollections.INSTANCE.configDir
        if (path.isNotEmpty()) {
            dir = dir.resolve(path)
        }
        val file = File(dir, filename)
        var value: T = default
        try {
            Files.createDirectories(RandomCollections.INSTANCE.configDir.toPath())
            if (file.exists()) {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = RandomCollections.INSTANCE.gsonPretty.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(RandomCollections.INSTANCE.gsonPretty.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T): Boolean {
        val dir = RandomCollections.INSTANCE.configDir
        val file = File(dir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(RandomCollections.INSTANCE.gsonPretty.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun attemptDefaultFileCopy(classLoader: ClassLoader, fileName: String) {
        val file = RandomCollections.INSTANCE.configDir.resolve(fileName)
        if (!file.exists()) {
            file.mkdirs()
            try {
                val stream = classLoader.getResourceAsStream("$assetPackage/$fileName")
                    ?: throw NullPointerException("File not found $fileName")

                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default file '$fileName': $e")
            }
        }
    }

    private fun attemptDefaultDirectoryCopy(classLoader: ClassLoader, directoryName: String) {
        val directory = RandomCollections.INSTANCE.configDir.resolve(directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
            try {
                val sourceUrl = classLoader.getResource("$assetPackage/$directoryName")
                    ?: throw NullPointerException("Directory not found $directoryName")
                val sourcePath = Paths.get(sourceUrl.toURI())

                Files.walk(sourcePath).use { stream ->
                    stream.forEach { sourceFile ->
                        val destinationFile = directory.resolve(sourcePath.relativize(sourceFile).toString())
                        if (Files.isDirectory(sourceFile)) {
                            // Create subdirectories in the destination
                            destinationFile.mkdirs()
                        } else {
                            // Copy files to the destination
                            Files.copy(sourceFile, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        }
                    }
                }
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default directory '$directoryName': " + e.message)
            }
        }
    }
}
