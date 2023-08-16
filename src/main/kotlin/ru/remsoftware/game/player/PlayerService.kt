package ru.remsoftware.game.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.starfarm.core.ApiManager
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.tinkoff.kora.common.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class PlayerService(
    private val database: DataBaseRepository,
    private val logger: Logger,
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
            val onlinePlayersList: MutableList<String> = mutableListOf()
            val playersInCache: MutableList<String> = mutableListOf()
            for (player in onlinePlayers) {
                onlinePlayersList.add(player.name)
            }
            for (player in players) {
                val name = player.key
                playersInCache.add(name)
            }
            val invalidateList = mutableListOf<String>()
            playersInCache.forEach {
                if (it !in onlinePlayersList) {
                    invalidateList.add(it)
                }
            }
            for (player in invalidateList) {
                invalidate(player)
            }
            if (invalidateList.isEmpty()) {
                logger.log("Недействительного кэша игроков не найдено")
            } else {
                logger.log("Список игроков удалённых из кэша: $invalidateList")
            }
            for (player in players) {
                database.updatePlayer(player.value)
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerPreJoin(event: AsyncPlayerPreLoginEvent) {
        val playerName = event.name
        val playerData = playerDataLoad(playerName)
        players[playerName] = playerData
        logger.log("Player data loaded for $playerName \n $playerData")
    }


    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val name = player.name
        database.updatePlayer(players[name]!!)
        logger.log("Update players data for $name")
        invalidate(name)
    }

    fun playerDataLoad(playerName: String): KitPlayer {
        val kitPlayer = PlayerLoader(playerName, database)
        return KitPlayer(kitPlayer.name, kitPlayer.kit, kitPlayer.money, kitPlayer.donateGroup, kitPlayer.arena, kitPlayer.currentKills, kitPlayer.kills, kitPlayer.deaths, kitPlayer.localBooster, kitPlayer.activeBooster, kitPlayer.boosterTime)
    }


}
