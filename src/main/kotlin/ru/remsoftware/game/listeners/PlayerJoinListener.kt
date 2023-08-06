package ru.remsoftware.game.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.remsoftware.game.money.boosters.BoosterManager
import ru.remsoftware.game.player.PlayerService
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PlayerJoinListener(
    private val playerService: PlayerService,
    private val boosterManager: BoosterManager,
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null
        val player = event.player
        val kitPlayer = playerService[player]!!
        playerService.loadScoreboard(player)

        if (kitPlayer.activeBooster) {
            boosterManager.createBooster(kitPlayer.boosterTime, true, player.name)
        }

    }
}