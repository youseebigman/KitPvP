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


    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (sender.isOp) {
            if (args.isEmpty() || args[0].equals("help", ignoreCase = true)) {
                ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&2&l Справка помощи для команды /kitpvp \n" +
                        "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp booster add [playerName] [minutes] &f- Выдать бустер игроку")
                return true
            } else {
                if (sender is Player) {
                    val player: Player = sender
                    if (args[0].equals("sign", ignoreCase = true)) {
                        if (args.size == 4) {
                            if (args[1].equals("create")) {
                                var reward = 100500
                                try {
                                    reward = args[2].toInt()
                                } catch (e: NumberFormatException) {
                                }
                                if (reward != 100500) {
                                    var cooldown = 100500L
                                    try {
                                        cooldown = args[3].toLong()
                                    } catch (e: NumberFormatException) {
                                    }
                                    if (cooldown != 100500L) {
                                        val signWorkers = signService.getWorkers()
                                        if (signWorkers.contains(sender.name)) {
                                            val selectedSign = signService.getSelectSign()
                                            if (selectedSign != null) {
                                                signService.createSign(database, selectedSign, reward, cooldown, player)
                                                ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a&L Вы успешно установили табличку")
                                            } else {
                                                ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c Вы не выбрали табличку!")
                                            }
                                        } else {
                                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c Вы не в режиме работы с табличками!")
                                        }
                                    } else {
                                        ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели кулдаун!")
                                    }
                                } else {
                                    ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели награду!")
                                }
                            }
                        }
                        if (args.size == 1 || args.size == 3) {
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
                    }
                    if (args[0].equals("booster", ignoreCase = true)) {
                        if (args.size == 1 || args.size == 2 || args.size == 3) {
                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы не ввели нужные данные! \n" +
                                    "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp booster add [playerName] [minutes]")
                            return true
                        }
                        if (args.size == 4) {
                            if (args[0].equals("booster", ignoreCase = true)) {
                                if (args[1].equals("add", ignoreCase = true)) {
                                    val target: Player? = Bukkit.getServer().getPlayer(args[2])
                                    var time = 100500L
                                    try {
                                        time = args[3].toLong()
                                    } catch (e: NumberFormatException) {
                                    }
                                    if (time != 100500L) {
                                        if (target == null) {
                                            ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Указанного игрока нету на сервере!")
                                            return true
                                        } else {
                                            val targetKitPlayer = playerService[args[2]]!!
                                            if (targetKitPlayer.activeBooster) {
                                                ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l У данного игрока уже активирован бустер!")
                                            } else {
                                                boosterManager.createBooster(TimeUnit.MINUTES.toMillis(time), true, target.name)
                                                targetKitPlayer.boosterTime = time
                                                ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&a&l Вы успешно выдали бустер игроку ${target.name} на $time минут")
                                                logger.log("${sender.name} выдал бустер игроку ${target.name}")
                                                return true
                                            }
                                        }
                                    } else {
                                        ChatUtil.sendMessage(sender, "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели время!")
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
