package ru.remsoftware.game.money.boosters

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class BoosterManager(
    private val playerService: PlayerService,
    private val dataBase: DataBaseRepository,
) {

    fun createBooster(duration: Long, isLocal: Boolean, playerName: String) {
        val currentTime = System.currentTimeMillis()
        val booster = Booster(
            currentTime,
            duration,
            TimeUnit.MILLISECONDS.toSeconds(duration),
            isLocal,
            playerName
        )
        val kitPlayer = playerService[playerName]!!
        kitPlayer.activeBooster = true
        kitPlayer.boosterTime = duration
        kitPlayer.localBooster = 1.5
        playerService[playerName] = kitPlayer
        dataBase.updatePlayer(kitPlayer)
        val player = Bukkit.getPlayer(playerName)
        startBooster(booster, player)

    }

    private fun startBooster(booster: Booster, player: Player) {
        val playerName = player.name
        val kitPlayer = playerService[playerName]!!

        GlobalTaskContext.everyAsync(1, 20) {
            val boosterRemainingTime = booster.remainingTime
            booster.remainingTime -= 1
            val kPlayer = playerService[playerName]
            if (kPlayer == null) {
                it.cancel()
            } else {
                kitPlayer.boosterTime = TimeUnit.SECONDS.toMillis(booster.remainingTime)
                if (boosterRemainingTime == 0L) {
                    kitPlayer.activeBooster = false
                    kitPlayer.localBooster = 1.0
                    playerService[playerName] = kitPlayer
                    dataBase.updatePlayer(kitPlayer)
                    ChatUtil.sendMessage(player, "&cВремя вашего бустера вышло!")
                    player.playSound(player.eyeLocation, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f)
                    it.cancel()
                }
            }
        }
    }
}

