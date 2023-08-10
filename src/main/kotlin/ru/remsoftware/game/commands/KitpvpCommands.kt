package ru.remsoftware.game.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.Tips
import ru.remsoftware.game.money.boosters.BoosterManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.utils.Logger
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class KitpvpCommands(
    private val playerService: PlayerService,
    private val boosterManager: BoosterManager,
    private val logger: Logger,
    private val signService: SignService,
    private val database: DataBaseRepository,
) : TabExecutor {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (command.name.lowercase().equals("kitpvp")) {
            if (args.size == 1) {
                return mutableListOf("booster", "sign")
            }
            if (args.size == 2) {
                if (args[0].equals("sign", ignoreCase = true)) {
                    return mutableListOf("work", "create", "update")
                }
                if (args[0].equals("booster", ignoreCase = true)) {
                    return mutableListOf("add")
                }
            }
            if (args.size == 3) {
                if (args[0].equals("booster", ignoreCase = true)) {
                    val onlinePlayers = Bukkit.getOnlinePlayers()
                    val playersList: MutableList<String> = mutableListOf()
                    for (player in onlinePlayers) {
                        playersList.add(player.name)
                    }
                    return playersList
                }
            }
        }
        return null
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.isOp) {
            if (args.isEmpty() || args[0].equals("help", ignoreCase = true)) {
                ChatUtil.sendMessage(sender, Tips.KITPVP_HELP_TIPS.tip)
                return true
            } else {
                if (sender is Player) {
                    val player: Player = sender


                    if (args[0].equals("sign", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 3) {
                            ChatUtil.sendMessage(sender, Tips.SIGN_TIPS.tip)
                            ChatUtil.sendMessage(sender, Tips.SIGNTIPS.tip)
                        }
                        if (args.size == 2) {
                            if (args[1].equals("work")) {
                                val signWorkers = signService.getWorkers()
                                if (!signWorkers.contains(sender.name)) {
                                    signService.setWorker(sender.name)
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a Вы приступили к работе с табличками")
                                } else {
                                    signService.invalidateWorker(sender.name)
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a Вы закончили работу с табличками")
                                }
                            }
                        }
                        if (args.size == 4) {
                            if (args[1].equals("create")) {
                                val signWorkers = signService.getWorkers()
                                if (signWorkers.contains(sender.name)) {
                                    var reward: Int? = null
                                    var cooldown: Long? = null

                                    try {
                                        reward = args[2].toInt()
                                    } catch (e: NumberFormatException) {
                                        ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели награду!")
                                    }
                                    try {
                                        cooldown = args[3].toLong()
                                    } catch (e: NumberFormatException) {
                                        ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели кулдаун!")
                                    }
                                    if (reward != null && cooldown != null) {

                                        val selectedSign = signService.getSelectSign()
                                        if (selectedSign != null) {
                                            signService.createSign(database, selectedSign, reward, cooldown, player)
                                        } else {
                                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c Вы не выбрали табличку!")
                                        }
                                    }
                                } else {
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c Вы не в режиме работы с табличками!")
                                }
                            }
                        }
                    }
                    if (args[0].equals("booster", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 2 || args.size == 3) {
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
                                            boosterManager.createBooster(TimeUnit.MINUTES.toMillis(time), true, targetKitPlayer.name)
                                            targetKitPlayer.boosterTime = time
                                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a&l Вы успешно выдали бустер игроку ${targetKitPlayer.name} на $time минут")
                                            logger.log("${sender.name} выдал бустер игроку ${targetKitPlayer.name}")
                                            return true
                                        }
                                    } else {
                                        ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Указанного игрока нету на сервере!")
                                    }
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
                                    ChatUtil.sendMessage(sender, "$targetKitPlayer")
                                } else {
                                    val kitPlayer = database.loadPlayerData(targetName)
                                    if (kitPlayer != null) {
                                        ChatUtil.sendMessage(sender, "$targetKitPlayer")
                                    } else {
                                        ChatUtil.sendMessage(sender, "&cДанный игрок никогда не заходил на сервер!")
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l У вас нету прав на использование данной команды")
            return true
        }
        return true
    }

}
