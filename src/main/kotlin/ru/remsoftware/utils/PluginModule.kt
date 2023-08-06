package ru.remsoftware.utils

import org.bukkit.plugin.java.JavaPlugin
import ru.remsoftware.Kitpvp
import ru.starfarm.core.CorePlugin
import ru.tinkoff.kora.common.Module

@Module
interface PluginModule {

    fun plugin(): CorePlugin = JavaPlugin.getPlugin(Kitpvp::class.java)

}