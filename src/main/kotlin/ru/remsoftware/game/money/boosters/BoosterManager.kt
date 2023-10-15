package ru.remsoftware.game.money.boosters

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class BoosterManager(
    private val playerService: PlayerService,
    private val dataBase: DataBaseRepository,
) {

    fun createBooster(duration: Int, isLocal: Boolean, playerName: String) {
        val currentTime = System.currentTimeMillis()
        val booster = Booster(
            currentTime,
            duration,
            duration,
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
        player.playSound(player.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)

    }

    fun removeBooster(player: Player) {
        val playerName = player.name
        val kitPlayer = playerService[playerName]!!
        kitPlayer.localBooster = 1.0
        kitPlayer.activeBooster = false
        kitPlayer.boosterTime = 0
        playerService[playerName] = kitPlayer
        dataBase.updatePlayer(kitPlayer)
        ChatUtil.sendMessage(player, "&&8[&b&lKit&4&lPvP&8]&c&lВремя вашего бустера вышло!")
        player.playSound(player.eyeLocation, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f)
    }

    private fun startBooster(booster: Booster, player: Player) {
        val playerName = player.name
        val kitPlayer = playerService[playerName]!!

        GlobalTaskContext.everyAsync(1, 20) {
            val boosterRemainingTime = booster.remainingTime
            booster.remainingTime -= 1
            if (!Bukkit.getServer().onlinePlayers.contains(player)) {
                it.cancel()
            } else {
                kitPlayer.boosterTime = booster.remainingTime
                if (boosterRemainingTime == 0) {
                    removeBooster(player)
                    it.cancel()
                }
            }
        }
    }
}

