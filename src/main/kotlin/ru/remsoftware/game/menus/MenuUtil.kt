package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.remsoftware.game.player.PlayerService
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class MenuUtil {
    val backButton = ApiManager.newItemBuilder(Material.ARROW).apply {
        name = "§cНазад"
        lore(
            "§7Нажмите, чтобы перейти назад"
        )
        addItemFlags(*ItemFlag.values())
    }.build()

    fun checkPlayerInventoryForBuy(player: Player, playerService: PlayerService): Boolean {
        val inventory = player.inventory
        val check = inventory.firstEmpty()
        val kitPlayer = playerService[player]!!
        var hasEmptySlot: Boolean = true
        var playerHasKit: Boolean = true
        if (check == -1) {
            player.closeInventory()
            player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c В вашем инвентаре нету места!")
            hasEmptySlot = false
        }
        if (kitPlayer.kit.equals("default")) {
            player.closeInventory()
            player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Сначала купите кит!")
            playerHasKit = false
        }
        return playerHasKit && hasEmptySlot
    }
}