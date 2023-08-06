package ru.remsoftware.utils.config

import com.typesafe.config.Config
import org.bukkit.plugin.java.JavaPlugin
import ru.starfarm.core.CorePlugin
import ru.tinkoff.kora.common.DefaultComponent
import ru.tinkoff.kora.common.Module


@Module
interface ConfigModule {

    fun pluginConfigLoader(plugin: CorePlugin) = PluginConfigLoader(plugin)

    @DefaultComponent
    fun config(loader: PluginConfigLoader): Config = loader.load("general.conf")
}