package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerService
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.tinkoff.kora.common.Component
import java.util.*

@Component
class KitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
) : InventoryContainer("Выбор кита", 6) {
    override fun drawInventory(player: Player) {
        val freeKitsItem = ApiManager.newItemBuilder(Material.LEATHER_CHESTPLATE).apply {
            name = "§fБесплатные киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        addItem(19, freeKitsItem) { _, _ ->
            FreeKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
        val cheapKitsItem = ApiManager.newItemBuilder(Material.CHAINMAIL_CHESTPLATE).apply {
            name = "§aДешёвые киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        addItem(21, cheapKitsItem) { _, _ ->
            CheapKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
        val averageKitsItem = ApiManager.newItemBuilder(Material.IRON_CHESTPLATE).apply {
            name = "§cНедорогие киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        addItem(23, averageKitsItem) { _, _ ->
            AverageKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
        val bestKitsItem = ApiManager.newItemBuilder(Material.DIAMOND_CHESTPLATE).apply {
            name = "§5Дорогие киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        addItem(25, bestKitsItem) { _, _ ->
            BestKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            MainMenu(kitManager, kitService, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
    }
}

@Component
class FreeKitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
) : InventoryContainer("Бесплатные киты", 6) {
    override fun drawInventory(player: Player) {
        val allFreeKits = Collections.unmodifiableCollection(kitService.freeKits.toSortedMap().values)
        allFreeKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(Material.valueOf(it.value.icon)).apply {
                name = "§f${it.value.name}"
                lore(
                    "§fЦена: §aБесплатно"
                )
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }.build()
            addItem(it.index, item) { _, _ ->
                kitManager.buyKit(player, it.value)
                closeInventory(player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
    }
}

@Component
class CheapKitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
) : InventoryContainer("Дешёвые киты", 6) {
    override fun drawInventory(player: Player) {
        val allCheapKits = Collections.unmodifiableCollection(kitService.cheapKitsMap.toSortedMap().values)
        allCheapKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(Material.valueOf(it.value.icon)).apply {
                name = "§a${it.value.name}"
                lore(
                    "§fЦена: §a$${it.value.price}"
                )
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }.build()
            addItem(it.index, item) { _, _ ->
                kitManager.buyKit(player, it.value)
                closeInventory(player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
    }
}

@Component
class AverageKitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
) : InventoryContainer("Недорогие киты", 6) {
    override fun drawInventory(player: Player) {
        val allAverageKits = Collections.unmodifiableCollection(kitService.averageKitsMap.toSortedMap().values)
        allAverageKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(Material.valueOf(it.value.icon)).apply {
                name = "§c${it.value.name}"
                lore(
                    "§fЦена: §a$${it.value.price}"
                )
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }.build()
            addItem(it.index, item) { _, _ ->
                kitManager.buyKit(player, it.value)
                closeInventory(player)
            }

        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
    }
}

@Component
class BestKitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
) : InventoryContainer("Дорогие киты", 6) {
    override fun drawInventory(player: Player) {
        val allBestKits = Collections.unmodifiableCollection(kitService.bestKitsMap.toSortedMap().values)
        allBestKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(Material.valueOf(it.value.icon)).apply {
                name = "§5${it.value.name}"
                lore(
                    "§fЦена: §a$${it.value.price}"
                )
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }.build()
            addItem(it.index, item) { _, _ ->
                kitManager.buyKit(player, it.value)
                closeInventory(player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService).openInventory(player)
        }
    }
}