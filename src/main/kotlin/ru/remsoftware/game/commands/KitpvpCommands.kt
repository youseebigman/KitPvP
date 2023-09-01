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
import ru.remsoftware.game.money.boosters.BoosterManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.parser.LocationParser
import ru.remsoftware.utils.Logger
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
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender
            if (player.isOp) {
                if (args.isEmpty() || args[0].equals("help", ignoreCase = true)) {
                    ChatUtil.sendMessage(sender, Tips.KITPVP_HELP_TIPS.tip)
                    return true
                } else {
                    // Booster commands
                    if (args[0].equals("booster", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 2 || args.size > 4) {
                            ChatUtil.sendMessage(sender, Tips.BOOSTER_TIPS.tip)
                            return true
                        }
                        if (args.size == 4) {
                            if (args[1].equals("add", ignoreCase = true)) {
                                var time: Long? = null
                                try {
                                    time = args[3].toLong()
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
                    if (args[0].equals("sign", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 3) {
                            ChatUtil.sendMessage(sender, Tips.SIGN_TIPS.tip)
                        }
                        if (args.size == 2) {
                            if (args[1].equals("work")) {
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
                        if (args.size == 4) {
                            if (args[1].equals("create")) {
                                val signWorkers = signService.getWorkers()
                                if (signWorkers.contains(player.name)) {
                                    var reward: Int? = null
                                    var cooldown: Long? = null
                                    try {
                                        reward = args[2].toInt()
                                    } catch (e: NumberFormatException) {
                                        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели награду!")
                                    }
                                    try {
                                        cooldown = args[3].toLong()
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
                            if (args[1].equals("update")) {
                                val signWorkers = signService.getWorkers()
                                if (signWorkers.contains(player.name)) {
                                    var reward: Int? = null
                                    var cooldown: Long? = null
                                    try {
                                        reward = args[2].toInt()
                                    } catch (e: NumberFormatException) {
                                        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы неправильно ввели награду!")
                                    }
                                    try {
                                        cooldown = args[3].toLong()
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
                        if (args.size == 1) {
                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&e /k server setspawn - установить спавн")
                        }
                        if (args.size == 2) {
                            if (args[1].equals("setspawn", ignoreCase = true)) {
                                val loc = player.location
                                serverInfoService.serverInfo!!.spawn = loc
                                database.updateSpawn(loc.world.name, locationParser.locToStr(loc))
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
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c&l У вас нету прав на использование данной команды")
                return true
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
                        var time: Long? = null
                        try {
                            time = args[3].toLong()
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
