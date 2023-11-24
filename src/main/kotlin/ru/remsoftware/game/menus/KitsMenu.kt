package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
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
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Выбор кита", 6) {
    override fun drawInventory(player: Player) {
        val freeKitsItem = ApiManager.newItemBuilder(Material.LEATHER_CHESTPLATE).apply {
            name = "§fБесплатные киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val cheapKitsItem = ApiManager.newItemBuilder(Material.CHAINMAIL_CHESTPLATE).apply {
            name = "§aДешёвые киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val averageKitsItem = ApiManager.newItemBuilder(Material.IRON_CHESTPLATE).apply {
            name = "§cНедорогие киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val bestKitsItem = ApiManager.newItemBuilder(Material.DIAMOND_CHESTPLATE).apply {
            name = "§5Дорогие киты"
            lore(
                "§7Нажмите, чтобы выбрать кит"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val donateKitsMenu = ApiManager.newItemBuilder(Material.GOLD_CHESTPLATE).apply {
            name = "§6Донат киты"
            lore(
                "",
                "&2Игроки с донатом могут выбрать кит бесплатно",
                "&2У каждого кита перезарядка 30 минут",
                "",
                "&2Обычные игроки могут купить кит за деньги",
                "",
                "&cВыбрать кит бесплатно можно будет через 3 дня после запуска сервера!"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val availableKitsItem = ApiManager.newItemBuilder(Material.CHEST).apply {
            name = "§2Ваши киты"
            lore(
                "§7Тут находятся киты, которые куплены у вас навсегда"
            )
        }.build()

        addItem(19, freeKitsItem) { _, _ ->
            FreeKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
        addItem(21, cheapKitsItem) { _, _ ->
            CheapKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
        addItem(23, averageKitsItem) { _, _ ->
            AverageKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
        addItem(25, bestKitsItem) { _, _ ->
            BestKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
        addItem(4, donateKitsMenu) { _, _ ->
            DonateKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
        addItem(31, availableKitsItem) { _, _ ->
            AvailableKitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }

        addItem(45, menuUtil.backButton) { _, _ ->
            MainMenu(kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
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
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Бесплатные киты", 6) {
    override fun drawInventory(player: Player) {
        val allFreeKits = Collections.unmodifiableCollection(kitService.freeKits.toSortedMap().values)
        allFreeKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.value.icon)).apply {
                name = "§f${it.value.name}"
                lore(
                    "",
                    "§fЦена: §aБесплатно"
                )
                addItemFlags(*ItemFlag.values())
            }.build()
            addItem(it.index, item) { _, _ ->
                kitManager.buyKit(player, it.value)
                closeInventory(player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
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
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Дешёвые киты", 6) {
    override fun drawInventory(player: Player) {
        val allCheapKits = Collections.unmodifiableCollection(kitService.cheapKitsMap.toSortedMap().values)
        allCheapKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.value.icon)).apply {
                name = "§a${it.value.name}"
                lore(
                    "",
                    "§fЦена: §a$${it.value.price}",
                    "",
                    "§2Вы можете купить кит навсегда за цену в 10 раз больше",
                    "§2Чтобы купить навсегда, нажмите §bПКМ"
                )
                addItemFlags(*ItemFlag.values())
            }.build()
            addItem(it.index, item) { _, inventoryClickListener ->
                if (inventoryClickListener.isRightClick) {
                    ConfirmBuyKitMenu(true, false, item, it.value, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                } else {
                    ConfirmBuyKitMenu(false, false, item, it.value, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                }
            }
            addItem(45, menuUtil.backButton) { _, _ ->
                KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
            }
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
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Недорогие киты", 6) {
    override fun drawInventory(player: Player) {
        val allAverageKits = Collections.unmodifiableCollection(kitService.averageKitsMap.toSortedMap().values)
        allAverageKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.value.icon)).apply {
                name = "§c${it.value.name}"
                lore(
                    "",
                    "§fЦена: §a$${it.value.price}",
                    "",
                    "§2Вы можете купить кит навсегда за цену в 10 раз больше",
                    "§2Чтобы купить навсегда, нажмите §bПКМ"
                )
                addItemFlags(*ItemFlag.values())
            }.build()
            addItem(it.index, item) { _, inventoryClickListener ->
                if (inventoryClickListener.isRightClick) {
                    ConfirmBuyKitMenu(true, false, item, it.value, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                } else {
                    ConfirmBuyKitMenu(false, false, item, it.value, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                }

            }
            addItem(45, menuUtil.backButton) { _, _ ->
                KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
            }
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
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Дорогие киты", 6) {
    override fun drawInventory(player: Player) {
        val allBestKits = Collections.unmodifiableCollection(kitService.bestKitsMap.toSortedMap().values)
        allBestKits.withIndex().forEach {
            val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.value.icon)).apply {
                name = "§5${it.value.name}"
                lore(
                    "",
                    "§fЦена: §a$${it.value.price}",
                    "",
                    "§2Вы можете купить кит навсегда за цену в 10 раз больше",
                    "§2Чтобы купить навсегда, нажмите §bПКМ"
                )
                addItemFlags(*ItemFlag.values())
            }.build()
            addItem(it.index, item) { _, inventoryClickListener ->
                if (inventoryClickListener.isRightClick) {
                    ConfirmBuyKitMenu(true, false, item, it.value, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                } else {
                    ConfirmBuyKitMenu(false, false, item, it.value, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                }
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
    }
}


class DonateKitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Донат киты", 6) {

    val eliteKitSlots = arrayOf(10, 19, 28, 36)
    val sponsorKitSlots = arrayOf(13, 22, 31, 39)
    val uniqueKitSlots = arrayOf(16, 25, 34, 38)
    val donateKits = Collections.unmodifiableCollection(kitService.donateKitsMap.values)
    override fun drawInventory(player: Player) {
        var eliteIndex = 0
        var sponsorIndex = 0
        var uniqueIndex = 0

        donateKits.forEach {
            if (it.donateGroup == 5) {
                val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.icon)).apply {
                    name = "§e${it.name}"
                    lore(
                        "",
                        "§fЦена: §a$${it.price}",
                        "&2Этот кит доступен для игроков с группой &e&lELITE",
                        "",
                        "§2Вы можете купить кит навсегда за цену в 10 раз больше",
                        "§2Чтобы купить навсегда, нажмите §bПКМ"
                    )
                    addItemFlags(*ItemFlag.values())
                }.build()
                addItem(eliteKitSlots[eliteIndex], item) { _, inventoryClickListener ->
                    val kitPlayer = playerService[player]!!
                    if (kitPlayer.donateGroup >= 5) {
                        if (inventoryClickListener.isRightClick) {
                            ConfirmBuyKitMenu(true, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        } else {
                            ConfirmBuyKitMenu(false, true, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        }
                    } else {
                        if (inventoryClickListener.isRightClick) {
                            ConfirmBuyKitMenu(true, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        } else {
                            ConfirmBuyKitMenu(false, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        }
                    }
                }
                eliteIndex++
            }
            else if (it.donateGroup == 7) {
                val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.icon)).apply {
                    name = "§6${it.name}"
                    lore(
                        "",
                        "§fЦена: §a$${it.price}",
                        "&2Этот кит доступен для игроков с группой &6&lSPONSOR",
                        "",
                        "§2Вы можете купить кит навсегда за цену в 10 раз больше",
                        "§2Чтобы купить навсегда, нажмите §bПКМ"
                    )
                    addItemFlags(*ItemFlag.values())
                }.build()
                addItem(sponsorKitSlots[sponsorIndex], item) { _, inventoryClickListener ->
                    val kitPlayer = playerService[player]!!
                    if (kitPlayer.donateGroup >= 7) {
                        if (inventoryClickListener.isRightClick) {
                            ConfirmBuyKitMenu(true, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        } else {
                            ConfirmBuyKitMenu(false, true, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        }
                    } else {
                        if (inventoryClickListener.isRightClick) {
                            ConfirmBuyKitMenu(true, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        } else {
                            ConfirmBuyKitMenu(false, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        }
                    }
                }
                sponsorIndex++
            }
            else if (it.donateGroup == 9) {
                val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.icon)).apply {
                    name = "§3${it.name}"
                    lore(
                        "",
                        "§fЦена: §a$${it.price}",
                        "&2Этот кит доступен для игроков с группой &3&lUNIQUE",
                        "",
                        "§2Вы можете купить кит навсегда за цену в 10 раз больше",
                        "§2Чтобы купить навсегда, нажмите §bПКМ"
                    )
                    addItemFlags(*ItemFlag.values())
                }.build()
                addItem(uniqueKitSlots[uniqueIndex], item) { _, inventoryClickListener ->
                    val kitPlayer = playerService[player]!!
                    if (kitPlayer.donateGroup >= 9) {
                        if (inventoryClickListener.isRightClick) {
                            ConfirmBuyKitMenu(true, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        } else {
                            ConfirmBuyKitMenu(false, true, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        }
                    } else {
                        if (inventoryClickListener.isRightClick) {
                            ConfirmBuyKitMenu(true, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        } else {
                            ConfirmBuyKitMenu(false, false, item, it, kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
                        }
                    }
                }
                uniqueIndex++
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
    }
}

class AvailableKitsMenu(
    private val kitService: KitService,
    private val kitManager: KitManager,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Ваши киты", 6) {

    override fun drawInventory(player: Player) {
        val playerName = player.name
        val availableKit = kitManager.getPlayerAvailableKits(playerName)
        availableKit?.withIndex()?.forEach {
            val item = ApiManager.newItemBuilder(inventoryParser.jsonToItem(it.value.icon)).apply {
                name = "§b${it.value.name}"
                lore(
                    "",
                    "§2Нажмите, чтобы выбрать этот кит"
                )
                addItemFlags(*ItemFlag.values())
            }.build()
            addItem(it.index, item) { _, _ ->
                kitManager.setKit(player, it.value)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
    }

}