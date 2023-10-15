package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.starfarm.core.util.item.lore
import ru.starfarm.core.util.item.name

class ShopMenu(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
) : InventoryContainer("Магазин", 6) {
    override fun drawInventory(player: Player) {
        val armorShopItem = ApiManager.newItemBuilder(Material.IRON_HELMET).apply {
            name = "§fМагазин доспехов и оружия"
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
        addItem(21, armorShopItem) { _, _ ->
            ArmorShop(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
        }
        addItem(23, armorEnchantShopItem) { _, _ ->
            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            MainMenu(kitManager, kitService, menuUtil, moneyManager).openInventory(player)
        }
    }
}

class ArmorShop(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
) : InventoryContainer("Покупка предметов", 6) {
    override fun drawInventory(player: Player) {
        val ironArmorPrice: Int = 500
        val ironSwordPrice: Int = 1000
        val sword = ApiManager.newItemBuilder(Material.IRON_SWORD).apply {
            lore(
                "",
                "&fЦена: &a$$ironSwordPrice",
                "§7Нажмите, чтобы купить железный шлем"
            )
        }.build()
        val helmet = ApiManager.newItemBuilder(Material.IRON_HELMET).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железный шлем"
            )
        }.build()
        val chestPlate = ApiManager.newItemBuilder(Material.IRON_CHESTPLATE).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железный нагрудник"
            )
        }.build()
        val leggings = ApiManager.newItemBuilder(Material.IRON_LEGGINGS).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железные поножи"
            )
        }.build()
        val boots = ApiManager.newItemBuilder(Material.IRON_BOOTS).apply {
            lore(
                "",
                "&fЦена: &a$$ironArmorPrice",
                "§7Нажмите, чтобы купить железные ботинки"
            )
        }.build()

        addItem(20, sword) { _, _ ->
            val check = menuUtil.checkPlayerInventory(player)
            if (check) {
                moneyManager.removeMoney(player, ironArmorPrice)
                player.inventory.addItem(ItemStack(Material.IRON_SWORD))
            }
        }
        addItem(21, helmet) { _, _ ->
            val check = menuUtil.checkPlayerInventory(player)
            if (check) {
                moneyManager.removeMoney(player, ironArmorPrice)
                player.inventory.addItem(ItemStack(Material.IRON_HELMET))
            }
        }
        addItem(22, chestPlate) { _, _ ->
            val check = menuUtil.checkPlayerInventory(player)
            if (check) {
                moneyManager.removeMoney(player, ironArmorPrice)
                player.inventory.addItem(ItemStack(Material.IRON_CHESTPLATE))
            }
        }
        addItem(23, leggings) { _, _ ->
            val check = menuUtil.checkPlayerInventory(player)
            if (check) {
                moneyManager.removeMoney(player, ironArmorPrice)
                player.inventory.addItem(ItemStack(Material.IRON_LEGGINGS))
            }
        }
        addItem(24, boots) { _, _ ->
            val check = menuUtil.checkPlayerInventory(player)
            if (check) {
                moneyManager.removeMoney(player, ironArmorPrice)
                player.inventory.addItem(ItemStack(Material.IRON_BOOTS))
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            ShopMenu(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
        }
    }
}

class ArmorEnchantShop(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val moneyManager: MoneyManager,
    private val menuUtil: MenuUtil,
) : InventoryContainer("Зачарование предметов", 6) {
    override fun drawInventory(player: Player) {
        val armorList = arrayListOf<ItemStack>()
        for (item in player.inventory) {
            if (item != null) {
                if (item.type.name.endsWith("SWORD") || item.type.name.endsWith("HELMET") || item.type.name.endsWith("CHESTPLATE") || item.type.name.endsWith("LEGGINGS") || item.type.name.endsWith("BOOTS") || item.type.name.equals("BOW")) {
                    armorList.add(item)
                }
            }
        }
        armorList.withIndex().forEach {
            val item = ApiManager.newItemBuilder(it.value).build()
            addItem(it.index, item) { _, _ ->
                EnchantMenu(item, menuUtil, moneyManager, kitManager, kitService).openInventory(player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            ShopMenu(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
        }
    }
}

class EnchantMenu(
    private val item: ItemStack,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val kitManager: KitManager,
    private val kitService: KitService,
) : InventoryContainer("Зачарование предметов", 3) {
    override fun drawInventory(player: Player) {
        if (item.type.name.endsWith("HELMET") || item.type.name.endsWith("CHESTPLATE") || item.type.name.endsWith("LEGGINGS") || item.type.name.endsWith("BOOTS")) {
            val ironProtectionPrices = listOf(250, 600, 1000, 1500)
            val itemMeta = item.itemMeta
            if (itemMeta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                val protectionLevel = itemMeta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL)
                for (i in protectionLevel + 1..4) {
                    val enchantItem = ApiManager.newItemBuilder(item).apply {
                        /*name = item.name
                        lore = item.lore*/
                        lore(
                            "",
                            "&fЦена: &a$${ironProtectionPrices[i - 1]}",
                            "§7Нажмите, чтобы наложить зачарование на этот предмет"
                        )
                        enchant(Enchantment.PROTECTION_ENVIRONMENTAL, i)
                    }.build()
                    val newItem = ApiManager.newItemBuilder(item).apply {
                        lore()
                        enchant(Enchantment.PROTECTION_ENVIRONMENTAL, i)
                    }.build()
                    addItem(i - 1, enchantItem) { _, _ ->
                        val check = menuUtil.checkPlayerInventory(player)
                        if (check) {
                            moneyManager.removeMoney(player, ironProtectionPrices[i - 1])
                            player.inventory.addItem(newItem)
                            player.inventory.remove(item)
                            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
                        }
                    }
                }
            } else {
                for (i in 1..4) {
                    val enchantItem = ApiManager.newItemBuilder(item).apply {
                        lore(
                            "",
                            "&fЦена: &a$${ironProtectionPrices[i - 1]}",
                            "§7Нажмите, чтобы купить это зачарование"
                        )
                        enchant(Enchantment.PROTECTION_ENVIRONMENTAL, i)
                    }.build()
                    val newItem = ApiManager.newItemBuilder(item).apply {
                        lore()
                        enchant(Enchantment.PROTECTION_ENVIRONMENTAL, i)
                    }.build()
                    addItem(i - 1, enchantItem) { _, _ ->
                        val check = menuUtil.checkPlayerInventory(player)
                        if (check) {
                            moneyManager.removeMoney(player, ironProtectionPrices[i - 1])
                            player.inventory.addItem(newItem)
                            player.inventory.remove(item)
                            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
                        }
                    }
                }
            }
        }
        addItem(18, menuUtil.backButton) { _, _ ->
            ArmorEnchantShop(kitManager, kitService, moneyManager, menuUtil).openInventory(player)
        }
    }
}