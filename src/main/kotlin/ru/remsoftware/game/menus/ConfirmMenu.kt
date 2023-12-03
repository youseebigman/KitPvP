package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitData
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.starfarm.core.util.item.name


class ConfirmBuyKitMenu(
    private val buyForever: Boolean,
    private val donate: Boolean,
    private val item: ItemStack,
    private val kitData: KitData,
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Меню подтверждения", 3) {
    override fun drawInventory(player: Player) {
        val confirmButton = ApiManager.newItemBuilder(Material.EMERALD_BLOCK).apply {
            name = "§aПодтвердить"
            lore(
                "",
                "§7Нажмите, чтобы подтвердить"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val declinedButton = ApiManager.newItemBuilder(Material.REDSTONE_BLOCK).apply {
            name = "§cОтказаться"
            lore(
                "",
                "§7Нажмите, чтобы отказаться"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val infoItem = ApiManager.newItemBuilder(inventoryParser.jsonToItem(kitData.icon)).apply {
            name = "${item.name}"
            if (donate) {
                lore(
                    "",
                    "§eВы уверены, что хотите получить этот кит?"
                )
            } else {
                lore(
                    "",
                    "§eВы уверены, что хотите купить этот кит?"
                )
            }
            if (buyForever) {
                lore(
                    "",
                    "§eВы уверены, что хотите купить этот кит навсегда?"
                )
            }
            addItemFlags(*ItemFlag.values())
        }.build()
        addItem(11, confirmButton) { _, _ ->
            if (donate) {
                kitManager.buyKit(player, kitData)
                player.closeInventory()
            } else if (buyForever) {
                kitManager.buyKitForever(player, kitData)
                player.closeInventory()
            } else  {
                kitManager.buyKit(player, kitData)
                player.closeInventory()
            }
        }
        addItem(13, infoItem)
        addItem(15, declinedButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
    }
}