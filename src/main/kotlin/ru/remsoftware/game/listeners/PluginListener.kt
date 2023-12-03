package ru.remsoftware.game.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.donate.DonateManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.player.PlayerCombatManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.server.CommandsHologram
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
    private val donateManager: DonateManager,
    private val arenaService: ArenaService,
    private val commandsHologram: CommandsHologram,
) : Listener {


    @EventHandler
    fun onPluginEnabled(event: PluginEnableEvent) {
        kitService.kitsLoader(logger)
        potionService.potionDataLoad(database, logger)
        donateManager.loadDonate()
        GlobalTaskContext.after(10) {
            serverInfoService.loadInfo(database, locParse)
            signService.moneySignsLoader(logger, database)
            val worlds = Bukkit.getWorlds()
            worlds.forEach {
                arenaService.loadArenaInfo(it.name)
            }
            it.cancel()
        }
        commandsHologram.createCommandHologram()
    }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        playerCombatManager.combatMap.clear()
        val serverInfo = serverInfoService.serverInfo!!
        if (serverInfo.spawn == null) {
            val serverData = ServerInfoData(null, serverInfo.globalBooster)
            database.updateServerData(serverData)
        } else {
            val serverData = ServerInfoData(locParse.locToStr(serverInfo.spawn!!), serverInfo.globalBooster)
            database.updateServerData(serverData)
        }
        for (player in playerService.all()) {
            playerService.savePlayerGameData(Bukkit.getPlayer(player.name))
            logger.log("Saving data for ${player.name}")
        }
        for (sign in signService.all()) {
            val signData = MoneySignData(locParse.locToStr(sign.location), sign.reward, sign.status, sign.cooldown, sign.remainingTime)
            database.updateSignData(signData)
        }
    }

}