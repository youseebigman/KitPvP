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
            .price(29)
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
            .price(59)
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
            .price(149)
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
            .price(299)
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
            .price(799)
            .register()

        ApiManager.newDonateBuilder(ChatUtil.color("&a&lСлучайный дешёвый кит навсегда"))
            .slot(21)
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
            .price(49)
            .register()

        ApiManager.newDonateBuilder(ChatUtil.color("&a&lСлучайный недорогой кит навсегда"))
            .slot(23)
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
            .price(299)
            .register()

        ApiManager.newDonateBuilder(ChatUtil.color("&a&lУвеличение бонуса"))
            .slot(40)
            .icon(ItemStack(Material.EMERALD))
            .description(
                "",
                "&7К вашему бонусу прибавится &a&l$500 &7монет",
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val playerPermissions = playerService.getDonatePermissions(name)!!
                val bonusPermission = playerPermissions["bonus"]!!
                playerPermissions["bonus"] = bonusPermission + 1
                playerService.setDonatePermissions(name, playerPermissions)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&2&l Поздравляем! Ваш бонус увеличился на $500 монет")
                logger.log("Игрок $name купил донат Увеличение бонуса")
            }
            .price(99)
            .register()

        /*ApiManager.newDonateBuilder(ChatUtil.color("&a&lДоступ к &e&lELITE китам"))
            .slot(29)
            .icon(ItemStack(Material.IRON_INGOT))
            .description(
                "",
                "&7Вы получите доступ к выбору &e&lELITE &7китов",
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val playerPermissions = playerService.getDonatePermissions(name)!!
                playerPermissions["donateKit"] = 1
                playerService.setDonatePermissions(name, playerPermissions)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&2&l Поздравляем! Вы купили доступ к &e&lELITE &2&lкитам")
                logger.log("Игрок $name купил донат Доступ к ELITE китам")
            }
            .price(149)
            .register()

        ApiManager.newDonateBuilder(ChatUtil.color("&a&lДоступ к &6&lSPONSOR китам"))
            .slot(31)
            .icon(ItemStack(Material.GOLD_INGOT))
            .description(
                "",
                "&7Вы получите доступ к выбору &6&lSPONSOR и &e&lELITE &7китов",
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val playerPermissions = playerService.getDonatePermissions(name)!!
                playerPermissions["donateKit"] = 2
                playerService.setDonatePermissions(name, playerPermissions)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&2&l Поздравляем! Вы купили доступ к &6&lSPONSOR &2&lкитам")
                logger.log("Игрок $name купил донат Доступ к SPONSOR китам")
            }
            .price(399)
            .register()

        ApiManager.newDonateBuilder(ChatUtil.color("&a&lДоступ к &3&lUNIQUE китам"))
            .slot(33)
            .icon(ItemStack(Material.DIAMOND))
            .description(
                "",
                "&7Вы получите доступ к выбору &3&lUNIQUE, &6&lSPONSOR, &e&lELITE &7китов",
            )
            .callback {
                val player = it.player ?: return@callback
                val name = it.playerName
                val playerPermissions = playerService.getDonatePermissions(name)!!
                playerPermissions["donateKit"] = 3
                playerService.setDonatePermissions(name, playerPermissions)
                ChatUtil.sendMessage(player,"&8[&b&lKit&4&lPvP&8]&2&l Поздравляем! Вы купили доступ к &3&lUNIQUE &2&lкитам")
                logger.log("Игрок $name купил донат Доступ к UNIQUE китам")
            }
            .price(699)
            .register()*/

    }
}