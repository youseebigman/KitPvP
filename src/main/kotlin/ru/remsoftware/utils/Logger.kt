package ru.remsoftware.utils

import org.bukkit.plugin.java.JavaPlugin
import ru.tinkoff.kora.common.Component
import java.util.logging.Logger

@Component
class Logger(
    private val plugin: JavaPlugin
) {

    private val logger: Logger = plugin.logger

    fun log(vararg messages: String) {
        for (message in messages) {
            logger.info(message)
        }
    }
}