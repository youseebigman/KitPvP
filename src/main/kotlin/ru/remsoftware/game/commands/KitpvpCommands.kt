package ru.remsoftware.game.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.Tips
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.menus.MainMenu
import ru.remsoftware.game.menus.MenuUtil
import ru.remsoftware.game.menus.PotionMenu
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.money.boosters.BoosterManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.potions.PotionManager
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.LocationParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class KitpvpCommands(
    private val playerService: PlayerService,
    private val boosterManager: BoosterManager,
    private val logger: Logger,
    private val database: DataBaseRepository,
    private val signService: SignService,
    private val locationParser: LocationParser,
    private val serverInfoService: ServerInfoService,
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val menuUtil: MenuUtil,
    private val potionManager: PotionManager,
    private val inventoryParser: InventoryParser,
    private val potionService: PotionService,
    private val potionEffectParser: PotionEffectParser,
    private val moneyManager: MoneyManager,
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender
            if (player.isOp) {
                if (args.isEmpty() || args[0].equals("help", ignoreCase = true)) {
                    ChatUtil.sendMessage(sender, Tips.KITPVP_HELP_TIPS.tip)
                    return true
                } else {
                    if (args[0].equals("menu", ignoreCase = true)) {
                        MainMenu(kitManager, kitService, menuUtil, moneyManager).openInventory(player)
                    }
                    if (args[0].equals("potions", ignoreCase = true)) {
                        PotionMenu(potionService, inventoryParser, potionEffectParser).openInventory(player)
                    }
                    if (args[0].equals("create", ignoreCase = true)) {
                        if (args.size > 1) {
                            if (args[1].equals("potion", ignoreCase = true)) {
                                if (args.size == 5) {
                                    if (args[2].equals("create", ignoreCase = true)) {
                                        var potionName = args[3]
                                        if (potionName.contains("_")) {
                                           potionName = potionName.replace("_", " ")
                                        }
                                        var potionCooldown: Long? = null
                                        val targetPotion = player.inventory.itemInMainHand
                                        try {
                                            potionCooldown = args[4].toLong()
                                        } catch (e: NumberFormatException) {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели кулдаун!")
                                        }
                                        if (potionCooldown != null && targetPotion != null) {
                                            potionManager.createPotion(potionName, potionCooldown, inventoryParser.itemToJson(targetPotion), player)
                                        }
                                    }
                                    if (args[2].equals("update", ignoreCase = true)) {
                                        val potionName = args[3]
                                        var potionCooldown: Long? = null
                                        val targetPotion = player.inventory.itemInMainHand
                                        try {
                                            potionCooldown = args[4].toLong()
                                        } catch (e: NumberFormatException) {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели кулдаун!")
                                        }
                                        if (potionCooldown != null) {
                                            potionManager.updatePotion(potionName, potionCooldown, inventoryParser.itemToJson(targetPotion), player)
                                        }
                                    }
                                }
                            }
                            if (args[1].equals("kit", ignoreCase = true)) {
                                if (args.size == 5) {
                                    if (args[2].equals("create", ignoreCase = true)) {
                                        val kitName = args[3]
                                        var kitPrice: Int? = null
                                        try {
                                            kitPrice = args[4].toInt()
                                        } catch (e: NumberFormatException) {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели цену!")
                                        }
                                        if (kitPrice != null) {
                                            kitManager.createKit(database, player, kitName, kitPrice)
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a Вы успешно создали кит $kitName!")
                                        }
                                    } else {
                                        ChatUtil.sendMessage(player, Tips.CREATE_TIPS.tip)
                                    }
                                    if (args[2].equals("update", ignoreCase = true)) {
                                        val kitName = args[3]
                                        var kitPrice: Int? = null
                                        try {
                                            kitPrice = args[4].toInt()
                                        } catch (e: NumberFormatException) {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели цену!")
                                        }
                                        if (kitPrice != null) {
                                            kitManager.createKit(database, player, kitName, kitPrice)
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a Вы успешно обновили кит $kitName!")
                                        }
                                    } else {
                                        ChatUtil.sendMessage(player, Tips.CREATE_TIPS.tip)
                                    }
                                } else {
                                    ChatUtil.sendMessage(player, Tips.CREATE_TIPS.tip)
                                }
                                if (args.size == 4) {
                                    if (args[2].equals("get", ignoreCase = true)) {
                                        val kit = kitService[args[3]]
                                        if (kit == null) {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Кита с таким названием не существует!")
                                        } else {
                                            kitManager.setKit(player, kit)
                                        }
                                    } else {
                                        ChatUtil.sendMessage(player, Tips.CREATE_TIPS.tip)
                                    }

                                }
                            }
                            if (args[1].equals("sign", ignoreCase = true)) {
                                if (args.size == 2 || args.size == 4) {
                                    ChatUtil.sendMessage(sender, Tips.CREATE_TIPS.tip)
                                }
                                if (args.size == 3) {
                                    if (args[2].equals("work")) {
                                        val signWorkers = signService.getWorkers()
                                        if (!signWorkers.contains(player.name)) {
                                            signService.setWorker(player.name)
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a Вы приступили к работе с табличками")
                                        } else {
                                            signService.invalidateWorker(player.name)
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a Вы закончили работу с табличками")
                                        }
                                    }
                                }
                                if (args.size == 5) {
                                    if (args[2].equals("create")) {
                                        val signWorkers = signService.getWorkers()
                                        if (signWorkers.contains(player.name)) {
                                            var reward: Int? = null
                                            var cooldown: Long? = null
                                            try {
                                                reward = args[3].toInt()
                                            } catch (e: NumberFormatException) {
                                                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели награду!")
                                            }
                                            try {
                                                cooldown = args[4].toLong()
                                            } catch (e: NumberFormatException) {
                                                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели кулдаун!")
                                            }
                                            if (reward != null && cooldown != null) {
                                                val selectedSign = signService.getSelectSign()
                                                if (selectedSign != null) {
                                                    signService.createSign(database, selectedSign, reward, cooldown, player)
                                                } else {
                                                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не выбрали табличку!")
                                                }
                                            }
                                        } else {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не в режиме работы с табличками!")
                                        }
                                    }
                                    if (args[2].equals("update")) {
                                        val signWorkers = signService.getWorkers()
                                        if (signWorkers.contains(player.name)) {
                                            var reward: Int? = null
                                            var cooldown: Long? = null
                                            try {
                                                reward = args[3].toInt()
                                            } catch (e: NumberFormatException) {
                                                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели награду!")
                                            }
                                            try {
                                                cooldown = args[4].toLong()
                                            } catch (e: NumberFormatException) {
                                                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели кулдаун!")
                                            }
                                            if (reward != null && cooldown != null) {
                                                val selectedSign = signService.getSelectSign()
                                                if (selectedSign != null) {
                                                    signService.updateSign(database, selectedSign, reward, cooldown, player)
                                                } else {
                                                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не выбрали табличку!")
                                                }
                                            }
                                        } else {
                                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не в режиме работы с табличками!")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Booster commands
                    if (args[0].equals("booster", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 2 || args.size > 4) {
                            ChatUtil.sendMessage(sender, Tips.BOOSTER_TIPS.tip)
                            return true
                        }
                        if (args.size == 4) {
                            if (args[1].equals("add", ignoreCase = true)) {
                                var time: Int? = null
                                try {
                                    time = args[3].toInt()
                                } catch (e: NumberFormatException) {
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели время!")
                                }
                                if (time != null) {
                                    val targetKitPlayer = playerService[args[2]]
                                    if (targetKitPlayer != null) {
                                        if (targetKitPlayer.activeBooster) {
                                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l У данного игрока уже активирован бустер!")
                                        } else {
                                            boosterManager.createBooster(time, true, targetKitPlayer.name)
                                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a&l Вы успешно выдали бустер игроку ${targetKitPlayer.name} на $time секунд")
                                            logger.log("${sender.name} выдал бустер игроку ${targetKitPlayer.name}")
                                            return true
                                        }
                                    } else {
                                        ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Указанного игрока нету на сервере!")
                                    }
                                }
                            }
                        }
                        if (args.size == 3) {
                            if (args[1].equals("remove", ignoreCase = true)) {
                                val targetPlayer = Bukkit.getPlayer(args[2])
                                if (targetPlayer != null) {
                                    boosterManager.removeBooster(targetPlayer)
                                    logger.log("${sender.name} удалил бустер игроку ${targetPlayer.name}")
                                } else {
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Указанного игрока нету на сервере!")
                                }
                            }
                        }
                    }
                    // Sign commands
                    if (args[0].equals("player", ignoreCase = true)) {
                        if (args.size == 3) {
                            if (args[1].equals("check", ignoreCase = true)) {
                                val targetName: String = args[2]
                                val targetKitPlayer = playerService[targetName]
                                if (targetKitPlayer != null) {
                                    ChatUtil.sendMessage(player, "$targetKitPlayer")
                                } else {
                                    val kitPlayer = database.loadPlayerData(targetName)
                                    if (kitPlayer != null) {
                                        ChatUtil.sendMessage(player, "$targetKitPlayer")
                                    } else {
                                        ChatUtil.sendMessage(player, "&cДанный игрок никогда не заходил на сервер!")
                                    }
                                }
                            }
                        }
                    }
                    if (args[0].equals("server", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 3) {
                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&e /k server setspawn - установить спавн \n &8[&b&lKit&4&lPvP&8]&e /k server kit [get|create|update] Название (Цена) - работа с китами")
                        }
                        if (args.size == 2) {
                            if (args[1].equals("setspawn", ignoreCase = true)) {
                                val loc = player.location
                                serverInfoService.serverInfo!!.spawn = loc
                                database.updateSpawn(loc.world.name, locationParser.locToStr(loc))
                            } else {
                                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&e /k server setspawn - установить спавн \n &8[&b&lKit&4&lPvP&8]&e /k server kit [get|create|update] Название (Цена) - работа с китами")
                            }
                        }
                    }
                    if (args[0].equals("playsound", ignoreCase = true)) {
                        if (args.size == 2) {
                            val sound = Sound.valueOf(args[1])
                            player.playSound(player.eyeLocation, sound, 1f, 1f)

                        }
                        if (args.size == 4) {
                            val sound = Sound.valueOf(args[1])
                            val volume = args[2].toFloat()
                            val pitch = args[3].toFloat()
                            player.playSound(player.eyeLocation, sound, volume, pitch)

                        }
                    }
                    if (args[0].equals("sup", ignoreCase = true)) {
                        if (args.size == 3) {
                            if (args[1].equals("sethealth", ignoreCase = true)) {
                                val hp = args[2].toDouble()
                                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = hp
                                player.health = hp
                            }
                        }
                        if (args.size == 4) {
                            val target = Bukkit.getPlayer(args[2])
                            if (target != null) {
                                val hp = args[2].toDouble()
                                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = hp
                                player.health = hp
                            } else {
                                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]е&cДанного игрока нету на сервере!")
                            }
                        }
                    }
                }
            } else {
                if (args[0].equals("menu", ignoreCase = true)) {
                    MainMenu(kitManager, kitService, menuUtil, moneyManager).openInventory(player)
                } else {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c&l У вас нету прав на использование данной команды")
                }
            }
        } else {
            // Commands for console
            if (args[0].equals("player", ignoreCase = true)) {
                if (args.size == 3) {
                    if (args[1].equals("check", ignoreCase = true)) {
                        val targetName: String = args[2]
                        val targetKitPlayer = playerService[targetName]
                        if (targetKitPlayer == null) {
                            val kitPlayer = database.loadPlayerData(targetName)
                            if (kitPlayer != null) {
                                ChatUtil.sendMessage(sender, "$kitPlayer")
                            } else {
                                ChatUtil.sendMessage(sender, "&cДанный игрок никогда не заходил на сервер!")
                            }
                        } else {
                            ChatUtil.sendMessage(sender, "$targetKitPlayer")
                        }
                    }
                }
            }
            if (args[0].equals("booster", ignoreCase = true)) {
                if (args.size == 1 || args.size == 2 || args.size > 4) {
                    ChatUtil.sendMessage(sender, Tips.BOOSTER_TIPS.tip)
                    return true
                }
                if (args.size == 4) {
                    if (args[1].equals("add", ignoreCase = true)) {
                        var time: Int? = null
                        try {
                            time = args[3].toInt()
                        } catch (e: NumberFormatException) {
                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели время!")
                        }
                        if (time != null) {
                            val targetKitPlayer = playerService[args[2]]
                            if (targetKitPlayer != null) {
                                if (targetKitPlayer.activeBooster) {
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l У данного игрока уже активирован бустер!")
                                } else {
                                    boosterManager.createBooster(time, true, targetKitPlayer.name)
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a&l Вы успешно выдали бустер игроку ${targetKitPlayer.name} на $time секунд")
                                    logger.log("${sender.name} выдал бустер игроку ${targetKitPlayer.name}")
                                    return true
                                }
                            } else {
                                ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Указанного игрока нету на сервере!")
                            }
                        }
                    }
                }
                if (args.size == 3) {
                    if (args[1].equals("remove", ignoreCase = true)) {
                        val targetPlayer = Bukkit.getPlayer(args[2])
                        if (targetPlayer != null) {
                            boosterManager.removeBooster(targetPlayer)
                            logger.log("${sender.name} удалил бустер игроку ${targetPlayer.name}")
                        } else {
                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Указанного игрока нету на сервере!")
                        }
                    }
                }
            }
        }
        return true
    }

}
