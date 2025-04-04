package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.starfarm.core.util.format.ChatUtil

class ShopMenu(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Магазин", 6) {
    override fun drawInventory(player: Player) {
        val armorShopItem = ApiManager.newItemBuilder(Material.IRON_HELMET).apply {
            name = "§fМагазин доспехов и еды"
            lore(
                "",
                "§7Нажмите, чтобы открыть магазин"
            )
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
        val armorEnchantShopItem = ApiManager.newItemBuilder(Material.IRON_HELMET).apply {
            name = "§fМагазин чар"
            lore(
                "",
                "§7Нажмите, чтобы выбрать броню для наложения чар"
            )
            enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }.build()
        val repairMenuItem = ApiManager.newItemBuilder(Material.ANVIL).apply {
            name = "§fПочинка предметов"
            lore(
                "",
                "§7Нажмите, чтобы выбрать предмет который вы хотите починить"
            )
        }.build()
        addItem(21, armorShopItem) { _, _ ->
            ArmorShop(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
        addItem(23, armorEnchantShopItem) { _, _ ->
            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
        addItem(31, repairMenuItem) { _, _ ->
            RepairMenu(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            MainMenu(kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
    }
}

class RepairMenu(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Починка предметов", 6) {
    override fun drawInventory(player: Player) {
        val playerData = playerService[player]!!
        val playerBalance = playerData.money
        val repairPrice = 120
        val repairAllPrice = 500
        val maxDurability: Short = 0
        val itemMap = hashMapOf<Int, ItemStack>()
        for ((index, item) in player.inventory.withIndex()) {
            if (item != null) {
                if (item.type.name.endsWith("SWORD") || item.type.name.endsWith("HELMET") || item.type.name.endsWith("CHESTPLATE") || item.type.name.endsWith("LEGGINGS") || item.type.name.endsWith("BOOTS") || item.type.name.equals("BOW") || item.type.name.endsWith("AXE") || item.type.name.endsWith("PICKAXE") || item.type.name.endsWith("HOE") || item.type.name.endsWith("SPADE")) {
                    itemMap[index] = item
                } else continue
            }
        }
        var counter = 0
        for (item in itemMap) {
            if (item.value.durability == maxDurability) {
                continue
            }
            val item1 = ApiManager.newItemBuilder(item.value).apply {
                lore(
                    "",
                    "§fЦена починки: §a$120 монет"
                )
            }.build()
            addItem(counter, item1) { _, _ ->
                if (playerBalance < repairPrice) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                    player.closeInventory()
                } else {
                    val newItem = ApiManager.newItemBuilder(item.value).apply {
                        durability = 0
                    }.build()
                    player.inventory.setItem(item.key, newItem)
                    moneyManager.removeMoneyBecauseBuy(player, repairPrice)
                    player.playSound(player.eyeLocation, Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f)
                    updateInventory(player)
                }
            }
            counter++
        }
        val item = ApiManager.newItemBuilder(Material.ENDER_PEARL).apply {
            name = "§aПочинить все предметы"
            lore(
                "",
                "§fЦена починки: §a$500 монет"
            )
        }.build()
        addItem(36, item) { _, _ ->
            if (playerBalance < repairPrice) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                for (item1 in itemMap) {
                    val newItem = ApiManager.newItemBuilder(item1.value).apply {
                        durability = 0
                    }.build()
                    player.inventory.setItem(item1.key, newItem)
                }
                moneyManager.removeMoneyBecauseBuy(player, repairAllPrice)
                player.playSound(player.eyeLocation, Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f)
                closeInventory(player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            ShopMenu(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
    }

}

class ArmorShop(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Покупка предметов", 6) {
    override fun drawInventory(player: Player) {
        val playerData = playerService[player]!!
        val playerBalance = playerData.money
        val ironArmorPrice: Int = 500
        val pumpPrice = 160
        val helmet = ApiManager.newItemBuilder(Material.IRON_HELMET).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железный шлем"
            )
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
        val chestPlate = ApiManager.newItemBuilder(Material.IRON_CHESTPLATE).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железный нагрудник"
            )
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
        val leggings = ApiManager.newItemBuilder(Material.IRON_LEGGINGS).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железные поножи"
            )
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
        val boots = ApiManager.newItemBuilder(Material.IRON_BOOTS).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железные ботинки"
            )
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }.build()
        val food = ApiManager.newItemBuilder(Material.PUMPKIN_PIE, 20).apply {
            lore(
                "",
                "&fЦена: &a$160 монет",
                "§7Нажмите, чтобы купить пирог"
            )
        }.build()

        addItem(21, helmet) { _, _ ->
            if (playerBalance < ironArmorPrice) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                val check = menuUtil.checkPlayerInventoryForBuy(player, playerService)
                if (check) {
                    moneyManager.removeMoneyBecauseBuy(player, ironArmorPrice)
                    player.inventory.addItem(ItemStack(Material.IRON_HELMET))
                }
            }
        }
        addItem(22, chestPlate) { _, _ ->
            if (playerBalance < ironArmorPrice) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                val check = menuUtil.checkPlayerInventoryForBuy(player, playerService)
                if (check) {
                    moneyManager.removeMoneyBecauseBuy(player, ironArmorPrice)
                    player.inventory.addItem(ItemStack(Material.IRON_CHESTPLATE))
                }
            }
        }
        addItem(23, leggings) { _, _ ->
            if (playerBalance < ironArmorPrice) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                val check = menuUtil.checkPlayerInventoryForBuy(player, playerService)
                if (check) {
                    moneyManager.removeMoneyBecauseBuy(player, ironArmorPrice)
                    player.inventory.addItem(ItemStack(Material.IRON_LEGGINGS))
                }
            }
        }
        addItem(24, boots) { _, _ ->
            if (playerBalance < ironArmorPrice) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                val check = menuUtil.checkPlayerInventoryForBuy(player, playerService)
                if (check) {
                    moneyManager.removeMoneyBecauseBuy(player, ironArmorPrice)
                    player.inventory.addItem(ItemStack(Material.IRON_BOOTS))
                }
            }
        }
        addItem(20, food) { _, _ ->
            if (playerBalance < pumpPrice) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                val check = menuUtil.checkPlayerInventoryForBuy(player, playerService)
                if (check) {
                    moneyManager.removeMoneyBecauseBuy(player, pumpPrice)
                    player.inventory.addItem(ItemStack(Material.PUMPKIN_PIE, 20))
                }
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            ShopMenu(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
    }
}

class ArmorEnchantShop(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Зачарование предметов", 6) {
    override fun drawInventory(player: Player) {
        val armorList = hashMapOf<Int, ItemStack>()
        for ((slot, item) in player.inventory.withIndex()) {
            if (item == null) {
                continue
            }
            else {
                if (item.type.name.endsWith("SWORD") || item.type.name.endsWith("HELMET") || item.type.name.endsWith("CHESTPLATE") || item.type.name.endsWith("LEGGINGS") || item.type.name.endsWith("BOOTS") || item.type.name.equals("BOW")) {
                    armorList[slot] = item
                } else continue
            }
        }
        var i = 0
        armorList.forEach {
            val item = it.value
            addItem(i, item) { _, _ ->
                EnchantMenu(item, it.key, menuUtil, moneyManager, kitManager, kitService, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
            }
            i++
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            ShopMenu(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
    }
}

class EnchantMenu(
    private val item: ItemStack,
    private val itemSlot: Int,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val playerManager: PlayerManager,
) : InventoryContainer("Зачарование предметов", 4) {
    override fun drawInventory(player: Player) {
        val durabilityPrice = listOf(100, 250, 500)

        if (item.type.name.endsWith("HELMET") || item.type.name.endsWith("CHESTPLATE") || item.type.name.endsWith("LEGGINGS") || item.type.name.endsWith("BOOTS")) {

            if (item.type.name.startsWith("LEATHER")) {
                val leatherProtectionPrices = listOf(50, 150, 250, 400, 600)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, leatherProtectionPrices, i - 1, 1, Enchantment.PROTECTION_ENVIRONMENTAL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("CHAINMAIL")) {
                val chainProtectionPrices = listOf(100, 300, 600, 900, 1200)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, chainProtectionPrices, i - 1, 1, Enchantment.PROTECTION_ENVIRONMENTAL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("GOLD")) {
                val goldProtectionPrices = listOf(50, 200, 400, 700, 950)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, goldProtectionPrices, i - 1, 1, Enchantment.PROTECTION_ENVIRONMENTAL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("IRON")) {
                val ironProtectionPrices = listOf(250, 600, 1000, 1500, 2000)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, ironProtectionPrices, i - 1, 1, Enchantment.PROTECTION_ENVIRONMENTAL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("DIAMOND")) {
                val diamondProtectionPrices = listOf(300, 900, 1500, 2000, 2700)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, diamondProtectionPrices, i - 1, 1, Enchantment.PROTECTION_ENVIRONMENTAL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            }
        } else if (item.type.name.endsWith("SWORD")) {

            if (item.type.name.startsWith("WOOD") || item.type.name.startsWith("GOLD")) {
                val woodAndGoldSharpnessPrice = listOf(80, 200, 350, 500, 700)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, woodAndGoldSharpnessPrice, i - 1, 1, Enchantment.DAMAGE_ALL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("STONE")) {
                val stoneSharpnessPrice = listOf(100, 250, 450, 700, 1000)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, stoneSharpnessPrice, i - 1, 1, Enchantment.DAMAGE_ALL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("IRON")) {
                val ironSharpnessPrice = listOf(150, 450, 900, 1200, 1500)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, ironSharpnessPrice, i - 1, 1, Enchantment.DAMAGE_ALL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            } else if (item.type.name.startsWith("DIAMOND")) {
                val diamondSharpnessPrice = listOf(200, 600, 1200, 1500, 2000)
                for (i in 1..5) {
                    createEnchantsItems(item, itemSlot, player, diamondSharpnessPrice, i - 1, 1, Enchantment.DAMAGE_ALL)
                }
                for (i in 1..3) {
                    createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
                }
            }
        } else if (item.type.name.endsWith("BOW")) {
            val bowPowerPrice = listOf(250, 600, 1000, 1500, 2000)
            for (i in 1..5) {
                createEnchantsItems(item, itemSlot, player, bowPowerPrice, i - 1, 1, Enchantment.ARROW_DAMAGE)
            }
            for (i in 1..3) {
                createEnchantsItems(item, itemSlot, player, durabilityPrice, i - 1, 2, Enchantment.DURABILITY)
            }
        }

        addItem(27, menuUtil.backButton) { _, _ ->
            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
        }
    }

    fun createEnchantsItems(item: ItemStack, itemSlot: Int, player: Player, priceList: List<Int>, counter: Int, row: Int, enchantment: Enchantment) {
        val playerData = playerService[player]!!
        val playerBalance = playerData.money
        val itemMeta = item.itemMeta
        var enchantLevel: Int? = null
        var slot: Int = 0
        when (row) {
            1 -> slot = counter
            2 -> slot = counter + 9
            3 -> slot = counter + 18
        }
        if (itemMeta.hasEnchant(enchantment)) {
            enchantLevel = itemMeta.getEnchantLevel(enchantment)
        }
        val enchantItem = ApiManager.newItemBuilder(item).apply {
            lore(
                "",
                "&fЦена: &a$${priceList[counter]}",
                "§7Нажмите, чтобы купить это зачарование"
            )
            enchant(enchantment, counter + 1)
        }.build()
        val newItem = ApiManager.newItemBuilder(item).apply {
            enchant(enchantment, counter + 1)
        }.build()
        addItem(slot, enchantItem) { _, _ ->
            val price = priceList[counter]
            if (playerBalance < price) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки!")
                player.closeInventory()
            } else {
                val check = menuUtil.checkPlayerInventoryForBuy(player, playerService)
                if (enchantLevel == null) {
                    if (check) {
                        moneyManager.removeMoneyBecauseBuy(player, price)
                        player.inventory.setItem(itemSlot, newItem)
                        ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
                    }
                } else {
                    if (enchantLevel < counter + 1) {
                        if (check) {
                            moneyManager.removeMoneyBecauseBuy(player, priceList[counter])
                            player.inventory.setItem(itemSlot, newItem)
                            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser, playerManager).openInventory(player)
                        }
                    } else {
                        player.closeInventory()
                        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c На вашем предмете уже наложены чары такого уровня!")
                        player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
                    }
                }
            }
        }
    }
}