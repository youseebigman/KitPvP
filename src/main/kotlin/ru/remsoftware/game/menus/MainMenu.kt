package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.donate.IDonateService
import ru.starfarm.core.donate.menu.DonateMenu
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.tinkoff.kora.common.Component

@Component
class MainMenu(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Меню", 6) {
    override fun drawInventory(player: Player) {
        val playerName = player.name
        val arenaItem = ApiManager.newItemBuilder(Material.GRASS).apply {
            name = "§fАрена"
            lore(
                "§7Нажмите, чтобы телепортироваться на арену"
            )
        }.build()
        val kitMenuItem = ApiManager.newItemBuilder(Material.IRON_SWORD).apply {
            name = "§fКиты"
            lore(
                "§7Нажмите, чтобы выбрать себе кит"
            )
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
        val shopItem = ApiManager.newItemBuilder(Material.APPLE).apply {
            name = "§fМагазин"
            lore(
                "§7Нажмите, чтобы открыть магазин"
            )
        }.build()
        val donateItem = ApiManager.newItemBuilder(Material.NETHER_STAR).apply {
            name = "§fДонат"
            lore(
                "§7Нажмите, чтобы открыть донат-магазин"
            )
        }.build()
        val bonusItem = ApiManager.newItemBuilder(Material.EMERALD).apply {
            name = "§2Бонус"
            lore(
                "§7Вы можете забирать бонус каждые 30 минут"
            )
        }.build()
        
        addItem(19, arenaItem) { _, _ ->
            ArenasMenu(kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
        addItem(21, kitMenuItem) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
        addItem(23, shopItem) { _, _ ->
            ShopMenu(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
        addItem(25, donateItem) { _, _ ->
            DonateMenu("§0Услуги режима", IDonateService.get().donates.values.filter { !it.global }).openInventory(player)
        }
        addItem(40, bonusItem) { _, _ ->
            playerManager.getBonus(player)
        }
    }

}
