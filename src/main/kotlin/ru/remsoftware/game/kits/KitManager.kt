package ru.remsoftware.game.kits

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.starfarm.core.util.time.CooldownUtil
import ru.tinkoff.kora.common.Component

@Component
class KitManager(
    private val kitService: KitService,
    private val playerService: PlayerService,
    private val inventoryParser: InventoryParser,
    private val potionEffectParser: PotionEffectParser,
    private val moneyManager: MoneyManager,
    private val logger: Logger,

    ) {

    fun getPlayerAvailableKits(name: String): ArrayList<KitData>? {
        val availableKitList = playerService.getAvailableKitList(name)
        val kitDataList = arrayListOf<KitData>()
        if (availableKitList == null) return null
        availableKitList.forEach {
            val kitData = kitService[it]!!
            kitDataList.add(kitData)
        }
        return kitDataList
    }

    fun setDonateKit(player: Player, data: KitData) {
        val name = data.name
        if (CooldownUtil.has(name, player)) {
            val timeLeft = NumberUtil.getTime(CooldownUtil.get(name, player))
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете взять этот кит через $timeLeft")
            player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
            player.closeInventory()
        } else {
            setKit(player, data)
            CooldownUtil.put(name, player, 1800000)
            logger.log("Игрок ${player.name} взял кит $name")
        }
    }

    fun buyKit(player: Player, data: KitData) {
        val kitPlayer = playerService[player]!!
        val availableKits = playerService.getAvailableKitList(player.name)
        if (kitPlayer.kit.equals(data.name)) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы уже выбрали себе такой кит!")
            player.closeInventory()
        } else {
            if (availableKits != null) {
                if (availableKits.contains(data.name)) {
                    setKit(player, data)
                    logger.log("Игрок ${player.name} выбрал свой кит ${data.name}")
                } else {
                    val playerBalance = kitPlayer.money
                    val price = data.price
                    if (price == 0) {
                        setKit(player, data)
                        logger.log("Игрок ${player.name} купил кит ${data.name}")
                    } else if (playerBalance < price) {
                        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки этого класса")
                        player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
                    } else {
                        moneyManager.removeMoneyBecauseBuy(player, price)
                        setKit(player, data)
                        logger.log("Игрок ${player.name} купил кит ${data.name}")
                    }
                }
            } else {
                val playerBalance = kitPlayer.money
                val price = data.price
                if (price == 0) {
                    setKit(player, data)
                    logger.log("Игрок ${player.name} купил кит ${data.name}")
                } else if (playerBalance < price) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки этого класса")
                    player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
                } else {
                    moneyManager.removeMoneyBecauseBuy(player, price)
                    setKit(player, data)
                    logger.log("Игрок ${player.name} купил кит ${data.name}")
                }
            }
        }
    }


fun buyKitForever(player: Player, data: KitData) {
    val kitPlayer = playerService[player]!!
    val availableKits = playerService.getAvailableKitList(player.name)
    if (availableKits != null) {
        if (availableKits.contains(data.name)) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас уже куплен этот кит навсегда!")
            player.closeInventory()
        } else {
            val playerBalance = kitPlayer.money
            val price = data.price * 10
            if (playerBalance < price) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки этого класса навсегда")
                player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
            } else {
                moneyManager.removeMoneyBecauseBuy(player, price)
                playerService.addAvailableKits(player.name, data.name)
                logger.log("Игрок ${player.name} купил кит ${data.name} навсегда. Текущий баланс: ${kitPlayer.money}")
            }
        }
    } else {
        val playerBalance = kitPlayer.money
        val price = data.price * 10
        if (playerBalance < price) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки этого класса навсегда")
            player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
        } else {
            moneyManager.removeMoneyBecauseBuy(player, price)
            playerService.addAvailableKits(player.name, data.name)
            logger.log("Игрок ${player.name} купил кит ${data.name} навсегда. Текущий баланс: ${kitPlayer.money}")
        }
    }
}

fun setKit(player: Player, data: KitData) {
    val kitPlayer = playerService.get(player)
    val kitName = data.name
    if (kitPlayer != null) {
        if (kitPlayer.kit.equals(kitName)) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы уже выбрали себе такой кит!")
            player.closeInventory()
        } else {
            val kitInventoryJson = data.inventory
            kitPlayer.kit = kitName
            kitPlayer.inventory = kitInventoryJson
            val kitInventory = inventoryParser.jsonToInventory(kitInventoryJson)
            val kitPotionEffect = potionEffectParser.jsonToPotionEffect(data.potionEffects)
            if (player.activePotionEffects != null) {
                val pEffects = player.activePotionEffects
                for (effect in pEffects) {
                    if (effect.type.equals(PotionEffectType.HEALTH_BOOST)) {
                        player.health = 20.0
                    }
                    player.removePotionEffect(effect.type)
                }
            }
            player.inventory.clear()
            for (item in kitInventory.withIndex()) {
                if (item.value == null) {
                    continue
                } else {
                    player.inventory.setItem(item.index, item.value)
                }
            }
            for (potionEffect in kitPotionEffect) {
                player.addPotionEffect(potionEffect)
            }
            player.closeInventory()
            data.numberOfPurchases++
            kitService[kitName] = data
            kitService.updateKitPurchases(kitName, data.numberOfPurchases)
        }
    }
}

fun createKit(player: Player, name: String, price: Int) {
    if (kitService[name] != null) {
        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Кит с таким названием уже существует")
    } else {
        val icon = inventoryParser.itemToJson(player.inventory.itemInOffHand).toString()
        player.inventory.clear(40)
        val kitInventory = inventoryParser.inventoryToJson(player.inventory)
        val kitPotionEffect = potionEffectParser.effectsToJson(player)
        val kitData = KitData(name, icon, kitInventory, kitPotionEffect, price, null, 0)
        kitService.createKit(kitData)
    }
}

fun updateKit(player: Player, name: String, price: Int) {
    val kitData = kitService[name]
    if (kitData == null) {
        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Кита с таким названием не существует")
    } else {
        val icon = inventoryParser.itemToJson(player.inventory.itemInOffHand).toString()
        player.inventory.clear(40)
        val kitInventory = inventoryParser.inventoryToJson(player.inventory)
        val kitPotionEffect = potionEffectParser.effectsToJson(player)
        val newKitData = KitData(name, icon, kitInventory, kitPotionEffect, price, null, kitData.numberOfPurchases)
        kitService.updateKit(newKitData)
    }
}
}