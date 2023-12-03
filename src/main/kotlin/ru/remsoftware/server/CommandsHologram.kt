package ru.remsoftware.server

import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.tinkoff.kora.common.Component

@Component
class CommandsHologram {
    private val commandsList = listOf(
        "§6Команды сервера:",
        "§e/spawn §f- Телепортироваться на спавн",
        "§e/k bonus §f- Получить бонус раз в 30 минут",
        "§e/k menu §f- Открыть меню режима",
    )
    fun createCommandHologram() {
        val hologram = ApiManager.createHologram(LocationUtil.fromString("lobby 0.5 112 -27.5"))
        commandsList.reversed().forEach { hologram.textLine(it) }
    }
}
