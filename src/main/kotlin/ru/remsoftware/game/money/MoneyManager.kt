package ru.remsoftware.game.money

import org.bukkit.Sound
import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.Logger
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class MoneyManager(
    private val database: DataBaseRepository,
    private val playerService: PlayerService,
    private val logger: Logger,
) {
    fun removeMoneyBecauseBuy(player: Player, amount: Int) {
        val playerName = player.name
        val playerData = playerService[playerName]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(playerName)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney - amount
            database.updateMoney(playerName, newMoney)
        } else {
            val currentMoney = playerData.money
            val newMoney = currentMoney - amount
            playerData.money = newMoney
            playerService[playerName] = playerData
            database.updateMoney(playerName, newMoney)
            val remainder = amount % 10
            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы потратили &a&l$amount &fмонет")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы потратили &a&l$amount &fмонету")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы потратили &a&l$amount &fмонеты")
            }
            player.playSound(player.eyeLocation, Sound.BLOCK_NOTE_CHIME, 1.0f, 2.0f,)
            logger.log("Игрок $playerName потратил $amount монет. Текущий баланс: ${playerData.money}")
        }
    }
    fun removeMoneyBecauseDeath(name: String, amount: Int) {
        val playerData = playerService[name]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(name)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney - amount
            database.updateMoney(name, newMoney)
        } else {
            val currentMoney = playerData.money
            val newMoney = currentMoney - amount
            playerData.money = newMoney
            playerService[name] = playerData
            database.updateMoney(name, newMoney)
            logger.log("Игрок $name потерял $amount монет за смерть. Текущий баланс: ${playerData.money}")
        }
    }
    fun addMoneyWithBoost(amount: Int, player: Player) {
        val playerName = player.name
        val playerData = playerService[playerName]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(playerName)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney + amount
            database.updateMoney(playerName, newMoney)
        } else {
            val currentMoney = playerData.money
            val playerBooster = playerData.localBooster
            val booster = playerBooster + playerService.getDonateGroupBooster(playerData.donateGroup)
            val bMoney = boostMoney(amount, booster)
            val newMoney = currentMoney + bMoney
            playerData.money = newMoney
            playerService[playerName] = playerData
            database.updateMoney(playerName, newMoney)
            val remainder = bMoney % 10
            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a&l$bMoney &fмонет")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a&l$bMoney &fмонету")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a&l$bMoney &fмонеты")
            }
            player.playSound(player.eyeLocation, Sound.BLOCK_NOTE_CHIME, 1.0f, 2.0f,)
            logger.log("Игрок $playerName получил $amount монет с бустером $booster. Текущий баланс: ${playerData.money}")
        }
    }
    fun addMoney(name: String, amount: Int) {
        val playerData = playerService[name]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(name)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney + amount
            database.updateMoney(name, newMoney)
            logger.log("Игрок $name получил $amount монет. Текущий баланс: $newMoney")
        } else {
            val currentMoney = playerData.money
            val newMoney = currentMoney + amount
            playerData.money = newMoney
            playerService[name] = playerData
            database.updateMoney(name, newMoney)
            logger.log("Игрок $name получил $amount монет. Текущий баланс: $newMoney")
        }
    }

    fun handleMoneyOnKill(victimMoney: Int): Int {
        return if (victimMoney < 20) {
            victimMoney + 40
        } else if (victimMoney in 20..50000) {
            victimMoney / 10 + 40
        } else {
            victimMoney / 20 + 40
        }

    }
    fun boostMoney(amount: Int, booster: Double) : Int {
        val boostMoney = amount * booster
        return boostMoney.toInt()
    }


}