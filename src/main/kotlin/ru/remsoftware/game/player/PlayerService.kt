package ru.remsoftware.game.player

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.inventories.InventoryManager
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.GameDataParser
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.LocationParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.task.GlobalTaskContext
import ru.tinkoff.kora.common.Component
import java.util.*

@Component
class PlayerService(
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val serverInfoService: ServerInfoService,
    private val locParse: LocationParser,
    private val inventoryParser: InventoryParser,
    private val potionEffectParser: PotionEffectParser,
    private val gameDataParser: GameDataParser,
    private val inventoryManager: InventoryManager,
) : Listener {

    private val players = hashMapOf<String, KitPlayer>()

    operator fun get(name: String) = players[name]

    operator fun get(player: Player) = get(player.name)

    fun invalidate(name: String) = players.remove(name)

    operator fun set(name: String, kitPlayer: KitPlayer) {
        players[name] = kitPlayer
    }

    fun all(): MutableCollection<KitPlayer> = Collections.unmodifiableCollection(players.values)

    init {
        GlobalTaskContext.every(600 * 20, 600 * 20) {
            val onlinePlayers = Bukkit.getOnlinePlayers()
            val onlinePlayersName: MutableList<String> = mutableListOf()
            val playersInCache: MutableList<String> = mutableListOf()
            val invalidateList = mutableListOf<String>()

            for (player in onlinePlayers) {
                onlinePlayersName.add(player.name)
            }
            for (player in players) {
                val name = player.key
                playersInCache.add(name)
            }
            playersInCache.forEach {
                if (it !in onlinePlayersName) {
                    database.updatePlayer(get(it)!!)
                    invalidateList.add(it)
                }
            }
            for (player in invalidateList) {
                invalidate(player)
            }
            for (player in players) {
                database.updatePlayer(player.value)
            }
            if (invalidateList.isEmpty()) {
                playersInCache.clear()
                logger.log("Недействительного кэша игроков не найдено")
            } else {
                logger.log("Список игроков удалённых из кэша: $invalidateList")
                playersInCache.clear()
                invalidateList.clear()
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerPreJoin(event: AsyncPlayerPreLoginEvent) {
        val playerName = event.name
        val playerData = playerDataLoad(playerName)
        players[playerName] = playerData
        logger.log("Player data loaded for $playerName")
    }


    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val name = event.player.name
        savePlayerGameData(event.player)
        logger.log("Update players data for $name")
        invalidate(name)
    }

    @EventHandler
    fun onPlayerKickEvent(event: PlayerKickEvent) {
        val name = event.player.name
        savePlayerGameData(event.player)
        logger.log("Update players data for $name")
        invalidate(name)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = serverInfoService.serverInfo!!.spawn
        inventoryManager.setDefaultInventory(event.player)
    }

    fun savePlayerGameData(player: Player) {
        val kitPlayer = players[player.name]!!
        kitPlayer.gameData = gameDataParser.gameDataToJson(player)
        kitPlayer.inventory = inventoryParser.inventoryToJson(player.inventory)
        kitPlayer.potionEffects = potionEffectParser.effectsToJson(player)
        kitPlayer.position = locParse.locToStr(player.location)
        database.updatePlayer(kitPlayer)
    }

    fun playerDataLoad(playerName: String): KitPlayer {
        val kitPlayer = PlayerLoader(playerName, database)
        return KitPlayer(
            kitPlayer.name,
            kitPlayer.gameData,
            kitPlayer.potionEffects,
            kitPlayer.kit,
            kitPlayer.money,
            kitPlayer.donateGroup,
            kitPlayer.arena,
            kitPlayer.currentKills,
            kitPlayer.kills,
            kitPlayer.deaths,
            kitPlayer.localBooster,
            kitPlayer.activeBooster,
            kitPlayer.boosterTime,
            kitPlayer.position,
            kitPlayer.inventory,
            kitPlayer.availableKits
        )
    }

    fun moveToSpawn(player: Player) {
        val spawn = serverInfoService.serverInfo!!.spawn
        if (spawn != null) player.teleport(spawn)
    }

    fun moveToOwnPosition(player: Player, pos: Location) {
        player.teleport(pos)
    }

}
