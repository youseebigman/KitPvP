package ru.remsoftware.game.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.utils.LocationParser
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component

@Component
class PluginListener(
    private val locParse: LocationParser,
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val signService: SignService,
    private val playerService: PlayerService,
) : Listener {

    @EventHandler
    fun onPluginEnabled(event: PluginEnableEvent) {
        signService.moneySignsLoader(logger, database)
    }
    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        for (player in playerService.all()) {
            database.updatePlayer(player)
            logger.log("Saving data for ${player.name}")
        }
        for (sign in signService.all()) {
            val signData = MoneySignData(locParse.locToStr(sign.location), sign.reward, sign.status, sign.cooldown, sign.remainingTime)
            database.updateSignData(signData)
        }
    }

}