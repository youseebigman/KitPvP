package ru.remsoftware.game.player

import org.bukkit.entity.Player
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.tinkoff.kora.common.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class PlayerScoreboard(
    private val playerService: PlayerService,
) {
    fun loadScoreboard(player: Player) {

        val kitPlayer = playerService[player]!!

        ApiManager.newScoreboardBuilder().apply {
            title = ChatUtil.color("&b&lKit&4&lPvP")
            setLine(11, ChatUtil.color("&3&l      ${LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm"))}"))
            setLine(10, "")
            setLine(9, ChatUtil.color("&a&lИнформация"))
            setLine(8, ChatUtil.color("  &fМонеты: &b${kitPlayer.money}"))
            setLine(7, ChatUtil.color("  &fУбийств: &b${kitPlayer.kills}"))
            setLine(6, ChatUtil.color("  &fСмертей: &b${kitPlayer.deaths}"))
            setLine(5, ChatUtil.color("  &fТекущие убийства: &b${kitPlayer.currentKills}"))
            if (kitPlayer.deaths == 0) {
                setLine(4, ChatUtil.color("  &fK/D: &b${kitPlayer.kills.toDouble() / 1}"))
            } else {
                setLine(4, ChatUtil.color("  &fK/D: &b${kitPlayer.kills.toDouble() / kitPlayer.deaths}"))
            }
            if (kitPlayer.activeBooster) {
                setLine(3, ChatUtil.color("  &fБустер: &a${NumberUtil.getTime(kitPlayer.boosterTime)}"))
            } else {
                setLine(3, ChatUtil.color("  &fБустер: &eНету"))
            }
            setLine(2, "")
            setLine(1, ChatUtil.color("       &bwww.starfarm.fun"))

            addUpdater(20) { _, scoreboard ->
                if (kitPlayer.activeBooster) {
                    scoreboard.setLine(3, ChatUtil.color("  &fБустер: &a${NumberUtil.getTime(kitPlayer.boosterTime)}"))
                } else {
                    scoreboard.setLine(3, ChatUtil.color("  &fБустер: &eНету"))
                }
            }
            addUpdater(60) { _, scoreboard ->
                scoreboard.setLine(11, ChatUtil.color("&3&l     ${LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm"))}"))
                val kd: Double = if (kitPlayer.deaths == 0) {
                    kitPlayer.kills.toDouble() / 1
                } else {
                    kitPlayer.kills.toDouble() / kitPlayer.deaths.toDouble()
                }
                scoreboard.setLine(8, ChatUtil.color("  &fМонеты: &b${kitPlayer.money}"))
                scoreboard.setLine(7, ChatUtil.color("  &fУбийств: &b${kitPlayer.kills}"))
                scoreboard.setLine(6, ChatUtil.color("  &fСмертей: &b${kitPlayer.deaths}"))
                scoreboard.setLine(5, ChatUtil.color("  &fТекущие убийства: &b${kitPlayer.currentKills}"))
                scoreboard.setLine(4, ChatUtil.color("  &fK/D: &b${"%.2f".format(kd)}"))
            }
        }.build(player)
    }
}