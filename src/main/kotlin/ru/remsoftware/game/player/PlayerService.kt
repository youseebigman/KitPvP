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
        val playerData = playerDataLoad(playerName)!!
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

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null
        val killer = event.entity.killer
        val victim = event.entity.player
        val ld = victim.lastDamageCause
        if (killer != null) {
            handleStatsOnKill(killer, victim)
        } else {
            val victimData = get(victim)!!
            victimData.deaths += 1
            set(victim.name, victimData)
            println("${victim.name} умер от $ld")
        }
    }

    fun loadScoreboard(player: Player) {
        val kitplayer = get(player)!!

        ApiManager.newScoreboardBuilder().apply {
            title = ChatUtil.color("&b&lKit&4&lPvP")
            setLine(11, ChatUtil.color("&3&l     ${LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss"))}"))
            setLine(10, "")
            setLine(9, ChatUtil.color("&a&lИнформация"))
            setLine(8, ChatUtil.color("  &fМонеты: &b${kitplayer.money}"))
            setLine(7, ChatUtil.color("  &fУбийств: &b${kitplayer.kills}"))
            setLine(6, ChatUtil.color("  &fСмертей: &b${kitplayer.deaths}"))
            setLine(5, ChatUtil.color("  &fТекущие убийства: &b${kitplayer.currentKills}"))
            setLine(4, ChatUtil.color("  &fK/D: &b${kitplayer.kills * kitplayer.deaths}"))
            if (kitplayer.activeBooster) {
                setLine(3, ChatUtil.color("  &fБустер: &a${NumberUtil.getTime(kitplayer.boosterTime)}"))
            } else {
                setLine(3, ChatUtil.color("  &fБустер: &eНету"))
            }
            setLine(2, "")
            setLine(1, ChatUtil.color("        &bwww.starfarm.fun"))

            addUpdater(20) { boardPlayer, scoreboard ->
                val kPlayer = get(boardPlayer)!!
                scoreboard.setLine(11, ChatUtil.color("&3&l     ${LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss"))}"))
                scoreboard.setLine(8, ChatUtil.color("  &fМонеты: &b${kPlayer.money}"))
                scoreboard.setLine(7, ChatUtil.color("  &fУбийств: &b${kPlayer.kills}"))
                scoreboard.setLine(6, ChatUtil.color("  &fСмертей: &b${kPlayer.deaths}"))
                scoreboard.setLine(5, ChatUtil.color("  &fТекущие убийства: &b${kPlayer.currentKills}"))
                scoreboard.setLine(4, ChatUtil.color("  &fK/D: &b${kPlayer.kills}"))
                if (kPlayer.activeBooster) {
                    scoreboard.setLine(3, ChatUtil.color("  &fБустер: &a${NumberUtil.getTime(kPlayer.boosterTime)}"))
                } else {
                    scoreboard.setLine(3, ChatUtil.color("  &fБустер: &eНету"))
                }
            }
        }.build(player)
    }

    fun playerDataLoad(playerName: String): KitPlayer {
        val kitPlayer = PlayerLoader(playerName, database)
        return KitPlayer(kitPlayer.name, kitPlayer.kit, kitPlayer.money, kitPlayer.donateGroup, kitPlayer.arena, kitPlayer.currentKills, kitPlayer.kills, kitPlayer.deaths, kitPlayer.localBooster, kitPlayer.activeBooster, kitPlayer.boosterTime)
    }

    private fun handleStatsOnKill(killer: Player, victim: Player) {
        val killerName = killer.name
        val victimName = victim.name
        val killerData = get(killerName)!!
        val victimData = get(victimName)!!
        val victimMoney = victimData.money
        val moneyForKill: Int = handleMoneyOnKill(victimMoney)

        killerData.currentKills += 1
        killerData.kills += 1
        victimData.currentKills = 0
        victimData.deaths += 1
        victimData.kit = "default"
        victimData.arena = "lobby"
        killerData.money += moneyForKill
        victimData.money -= moneyForKill

        if (victimMoney < 20) {
            sendMessageWithVariants(victimMoney, victim, "death")
        }
        if (victimMoney > 20) {
            sendMessageWithVariants(victimMoney, victim, "death")
        }
        sendMessageWithVariants(moneyForKill, killer, "kill")

        if (victimData.money < 0) victimData.money = 0

        players[killerName] = killerData
        players[victimName] = victimData
        database.updatePlayer(killerData)
        database.updatePlayer(victimData)
        logger.log("Player ${victim.name} was killed by ${killer.name}")
    }

    private fun sendMessageWithVariants(divisible: Int, player: Player, reason: String) {
        if (reason.equals("death")) {
            val remainder = divisible % 10

            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монет за смерть")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монету за смерть")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монеты за смерть")
            }
        }
        if (reason.equals("kill")) {
            val remainder = divisible % 10

            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монеты за убийство игрока")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монеты за убийство игрока")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монеты за убийство игрока")
            }
        }
    }
    private fun handleMoneyOnKill(victimMoney: Int) = if (victimMoney >= 50000) victimMoney / 20 + 20 else victimMoney / 10 + 20
}
