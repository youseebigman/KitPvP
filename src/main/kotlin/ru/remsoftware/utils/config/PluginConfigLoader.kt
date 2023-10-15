package ru.remsoftware.utils.config

import com.google.common.base.Preconditions
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import ru.starfarm.core.CorePlugin
import java.nio.file.Files

class PluginConfigLoader(private val plugin: CorePlugin) {

    fun load(fileName: String): Config {
        val dataFolder = plugin.dataFolder.toPath()
        Files.createDirectories(dataFolder)
        val filePath = dataFolder.resolve(fileName)
        if (Files.notExists(filePath)) {
            val resource = plugin.getResource(fileName)
            Preconditions.checkState(resource != null, "Resource %s not found", fileName)
            Files.copy(resource, filePath)
        }
        Files.newBufferedReader(filePath).use { reader -> return ConfigFactory.parseReader(reader).resolve() }
    }

}