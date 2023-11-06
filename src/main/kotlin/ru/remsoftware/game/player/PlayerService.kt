package ru.remsoftware.game.player

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
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
import ru.remsoftware.game.kits.KitData
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.GameDataParser
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.LocationParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.profile.group.DonateGroup
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component
import java.util.*
import kotlin.collections.ArrayList

@Component
class PlayerService(
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val locParse: LocationParser,
    private val inventoryParser: InventoryParser,
    private val potionEffectParser: PotionEffectParser,
    private val gameDataParser: GameDataParser,
    private val playerCombatManager: PlayerCombatManager,
) : Listener {

    private val players = hashMapOf<String, KitPlayer>()
    private val playerAvailableKits = hashMapOf<String, ArrayList<String>?>()

    operator fun get(name: String) = players[name]

    operator fun get(player: Player) = get(player.name)

    fun invalidate(name: String) = players.remove(name)

    operator fun set(name: String, kitPlayer: KitPlayer) {
        players[name] = kitPlayer
    }
    fun all(): MutableCollection<KitPlayer> = Collections.unmodifiableCollection(players.values)

    fun getAvailableKitList(name: String) = playerAvailableKits[name]

    fun loadAvailableKit(name: String, kits: String) {
        val kitList: List<String> = kits.split(":")
        val kitArrayList = arrayListOf<String>()
        kitList.forEach {
            kitArrayList.add(it)
        }
        playerAvailableKits[name] = kitArrayList
    }

    fun addAvailableKits(name: String, kitName: String) {
        val playerData = players[name]!!
        val currentKits = playerAvailableKits[name]
        if (currentKits == null) {
            playerAvailableKits[name] = arrayListOf(kitName)
            playerData.availableKits = kitName
        } else {
            currentKits.add(kitName)
            playerAvailableKits[name] = currentKits
            playerData.availableKits = returnAvailableKits(currentKits)
        }
    }

    fun returnAvailableKits(kits: ArrayList<String>): String {
        return kits.joinToString(separator = ":")
    }

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
        val availableKits = playerData.availableKits
        if (availableKits != null) {
            loadAvailableKit(playerName, availableKits)
        }
        logger.log("Player data loaded for $playerName")
    }

    fun savePlayerGameData(player: Player) {
        val name = player.name
        val kitPlayer = players[name]!!
        val availableKits = playerAvailableKits[name]
        if (playerCombatManager.isCombatPlayer(name)) {
            kitPlayer.gameData = null
            kitPlayer.inventory = null
            kitPlayer.potionEffects = null
            kitPlayer.position = null
            database.updatePlayer(kitPlayer)
        } else {
            kitPlayer.gameData = gameDataParser.gameDataToJson(player)
            kitPlayer.inventory = inventoryParser.inventoryToJson(player.inventory)
            kitPlayer.potionEffects = potionEffectParser.effectsToJson(player)
            kitPlayer.position = locParse.locToStr(player.location)
            database.updatePlayer(kitPlayer)
        }
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
            kitPlayer.kills,
            kitPlayer.currentKills,
            kitPlayer.deaths,
            kitPlayer.localBooster,
            kitPlayer.activeBooster,
            kitPlayer.boosterTime,
            kitPlayer.position,
            kitPlayer.inventory,
            kitPlayer.availableKits
        )
    }
    fun getDonateGroupBooster(donateGroup: Int): Double {
        return when (donateGroup) {
            in 1..2 -> 0.05
            3 -> 0.1
            4 -> 0.15
            5 -> 0.2
            6 -> 0.27
            7 -> 0.35
            8 -> 0.4
            9 -> 0.5
            else -> 0.0
        }
    }
}
