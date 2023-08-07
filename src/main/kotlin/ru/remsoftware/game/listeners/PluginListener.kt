package ru.remsoftware.game.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component

@Component
class PluginListener(
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val signService: SignService,
) : Listener {

    @EventHandler
    fun onPluginEnabled(event: PluginEnableEvent) {
        signService.moneySignsLoader(logger, database)
    }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        for (sign in signService.moneySignsCache) {
            val signEntity = sign.value
            val signData = MoneySignData(signService.locToStr(signEntity.location), signEntity.reward, signEntity.status, signEntity.cooldown, signEntity.remainingTime)
            database.updateSignData(signData)
        }
    }

}