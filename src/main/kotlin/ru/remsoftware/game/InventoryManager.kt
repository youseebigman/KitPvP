package ru.remsoftware.game

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.starfarm.core.util.item.name
import ru.tinkoff.kora.common.Component

@Component
class InventoryManager {
    private var itemMenu: ItemStack

    init {
        itemMenu = ItemStack(Material.COMPASS)
        val itemMeta = itemMenu.itemMeta
        itemMeta.displayName = "Меню"
        itemMenu.itemMeta = itemMeta
    }

    fun setDefaultInventory(player: Player) {
        player.inventory.setItem(0, itemMenu)
    }


}