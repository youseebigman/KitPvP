package ru.remsoftware.game.listeners

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.player.PlayerCombatManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.server.ServerInfoData
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.LocationParser
import ru.starfarm.core.task.GlobalTaskContext
import ru.tinkoff.kora.common.Component

@Component
class PluginListener(
    private val locParse: LocationParser,
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val signService: SignService,
    private val playerService: PlayerService,
    private val serverInfoService: ServerInfoService,
    private val kitService: KitService,
    private val potionService: PotionService,
    private val playerCombatManager: PlayerCombatManager,
) : Listener {


    @EventHandler
    fun onPluginEnabled(event: PluginEnableEvent) {
        kitService.kitsLoader(database, logger)
        potionService.potionDataLoad(database, logger)
        GlobalTaskContext.after(10) {
            serverInfoService.serverInfo = serverInfoService.loadInfo(database, locParse)
            signService.moneySignsLoader(logger, database)
            it.cancel()
        }

    }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        playerCombatManager.combatMap.clear()
        val serverInfo = serverInfoService.serverInfo
        if (serverInfo!!.spawn == null) {
            val serverData = ServerInfoData(null, serverInfo.globalBooster)
            database.updateServerData(serverData)
        } else {
            val serverData = ServerInfoData(locParse.locToStr(serverInfo.spawn!!), serverInfo.globalBooster)
            database.updateServerData(serverData)
        }
        for (player in playerService.all()) {
            database.updatePlayer(player)
            playerService.savePlayerGameData(Bukkit.getPlayer(player.name))
            logger.log("Saving data for ${player.name}")
        }
        for (sign in signService.all()) {
            val signData = MoneySignData(locParse.locToStr(sign.location), sign.reward, sign.status, sign.cooldown, sign.remainingTime)
            database.updateSignData(signData)
        }
    }

}