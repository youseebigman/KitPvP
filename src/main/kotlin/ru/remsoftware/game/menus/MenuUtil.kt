package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
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

    fun checkPlayerInventory(player: Player): Boolean {
        val inventory = player.inventory
        val check = inventory.firstEmpty()
        if (check == -1) {
            player.closeInventory()
            player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&cВ вашем инвентаре нету места!")
            return false
        } else {
            return true
        }
    }
}