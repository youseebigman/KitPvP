package ru.remsoftware.game.donate

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.boosters.BoosterManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.Logger
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class DonateManager(
    private val playerService: PlayerService,
    private val boosterManager: BoosterManager,
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val logger: Logger,
) {
    fun loadDonate() {
        ApiManager.newDonateBuilder(ChatUtil.color("&a&lЛокальный бустер монет #1"))
            .slot(9)
            .icon(ItemStack(Material.EXP_BOTTLE))
            .description(
                "",
                "&7Увеличивает получаемые вами",
                "&7монеты в &c1.5 раза &7на 30 игровых минут"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val time = 1800
                boosterManager.createBooster(time, true, name)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&a Вы успешно приобрели бустер на 30 минут!")
                logger.log("Игрок $name купил донат Локальный бустер монет #1")
            }
            .price(39)
            .register()
        ApiManager.newDonateBuilder(ChatUtil.color("&a&lЛокальный бустер монет #2"))
            .slot(11)
            .icon(ItemStack(Material.EXP_BOTTLE))
            .description(
                "",
                "&7Увеличивает получаемые вами",
                "&7монеты в &c1.5 раза &7на 60 игровых минут"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val time = 3600
                boosterManager.createBooster(time, true, name)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&a Вы успешно приобрели бустер на 60 минут!")
                logger.log("Игрок $name купил донат Локальный бустер монет #2")
            }
            .price(79)
            .register()

        ApiManager.newDonateBuilder(ChatUtil.color("&a&lЛокальный бустер монет #3"))
            .slot(13)
            .icon(ItemStack(Material.EXP_BOTTLE))
            .description(
                "",
                "&7Увеличивает получаемые вами",
                "&7монеты в &c1.5 раза &7на 4 игровых часа"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val time = 14400
                boosterManager.createBooster(time, true, name)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&a Вы успешно приобрели бустер на 4 часа!")
                logger.log("Игрок $name купил донат Локальный бустер монет #3")
            }
            .price(199)
            .register()
        ApiManager.newDonateBuilder(ChatUtil.color("&a&lЛокальный бустер монет #4"))
            .slot(15)
            .icon(ItemStack(Material.EXP_BOTTLE))
            .description(
                "",
                "&7Увеличивает получаемые вами",
                "&7монеты в &c1.5 раза &7на 12 игровых часов"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val time = 43200
                boosterManager.createBooster(time, true, name)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&a Вы успешно приобрели бустер на 12 часов!")
                logger.log("Игрок $name купил донат Локальный бустер монет #4")
            }
            .price(399)
            .register()
        ApiManager.newDonateBuilder(ChatUtil.color("&a&lЛокальный бустер монет #5"))
            .slot(17)
            .icon(ItemStack(Material.EXP_BOTTLE))
            .description(
                "",
                "&7Увеличивает получаемые вами",
                "&7монеты в &c1.5 раза &7на 7 игровых дней"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val time = 604800
                boosterManager.createBooster(time, true, name)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&a Вы успешно приобрели бустер на 7 дней!")
                logger.log("Игрок $name купил донат Локальный бустер монет #5")
            }
            .price(999)
            .register()
        ApiManager.newDonateBuilder(ChatUtil.color("&a&lСлучайный дешёвый кит навсегда"))
            .slot(28)
            .icon(ItemStack(Material.CHAINMAIL_CHESTPLATE))
            .description(
                "",
                "&7Вы получите случайный дешёвый кит навсегда",
                "&7Вы не можете получить кит, который у вас уже есть",
                "",
                "&cВНИМАНИЕ! Если у вас уже куплены все киты, не покупайте этот донат!"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val availableKits = playerService.getAvailableKitList(name)
                val randomKitList = arrayListOf<String>()
                val cheapKitMap = kitService.cheapKitsMap
                for (kit in cheapKitMap) {
                    val kitName = kit.key
                    if (availableKits != null) {
                        if (!availableKits.contains(kitName)) randomKitList.add(kitName) else continue
                    } else randomKitList.add(kitName)
                }
                val randomKit = randomKitList.randomOrNull() ?: return@callback
                playerService.addAvailableKits(name, randomKit)
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&2&l Поздравляем! Вы получили кит $randomKit")
                logger.log("Игрок $name купил донат Случайный дешёвый кит навсегда")
            }
            .price(99)
            .register()
        ApiManager.newDonateBuilder(ChatUtil.color("&a&lСлучайный недорогой кит навсегда"))
            .slot(30)
            .icon(ItemStack(Material.IRON_CHESTPLATE))
            .description(
                "",
                "&7Вы получите случайный недорогой кит навсегда",
                "&7Вы не можете получить кит, который у вас уже есть",
                "",
                "&cВНИМАНИЕ! Если у вас уже куплены все киты, не покупайте этот донат!"
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val availableKits = playerService.getAvailableKitList(name)
                val randomKitList = arrayListOf<String>()
                val averageKitMap = kitService.averageKitsMap
                for (kit in averageKitMap) {
                    val kitName = kit.key
                    if (availableKits != null) {
                        if (!availableKits.contains(kitName)) randomKitList.add(kitName) else continue
                    } else randomKitList.add(kitName)
                }
                val randomKit = randomKitList.randomOrNull() ?: return@callback
                playerService.addAvailableKits(name, randomKit)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&2&l Поздравляем! Вы получили кит &b$randomKit")
                logger.log("Игрок $name купил донат Случайный дешёвый кит навсегда")
            }
            .price(399)
            .register()
    }
}