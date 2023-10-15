package ru.remsoftware.game.listeners

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.server.ServerInfoData
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.LocationParser
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
) : Listener {
    var world: World? = null

    @EventHandler
    fun onPluginEnabled(event: PluginEnableEvent) {
        world = Bukkit.getServer().getWorld("world")
        serverInfoService.serverInfo = serverInfoService.loadInfo(world!!, database, locParse)
        signService.moneySignsLoader(logger, database)
        kitService.kitsLoader(database, logger)
        potionService.potionDataLoad(database, logger)
    }
    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        val serverInfo = serverInfoService.serverInfo
        if (serverInfo!!.spawn == null) {
            val serverData = ServerInfoData(null, serverInfo.globalBooster)
            database.updateServerData(world!!.name, serverData)
        } else {
            val serverData = ServerInfoData(locParse.locToStr(serverInfo.spawn!!), serverInfo.globalBooster)
            database.updateServerData(world!!.name, serverData)
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