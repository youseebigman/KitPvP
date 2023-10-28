package ru.remsoftware.game.commands

import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import ru.remsoftware.utils.PlayersUtil
import ru.remsoftware.utils.SortingUtil
import ru.tinkoff.kora.common.Component

@Component
class KitpvpTabComplete : TabCompleter {
    private val enchantmentList = arrayListOf<String>()
    private val soundList = arrayListOf<String>()
    init {
        Sound.values().forEach {
            soundList.add(it.toString())
        }
        Enchantment.values().forEach {
            enchantmentList.add(it.name.toString())
        }
    }


    override fun onTabComplete(sender: CommandSender?, command: Command, alias: String?, args: Array<out String>): MutableList<String>? {
        if (command.name.lowercase().equals("kitpvp")) {
            if (sender is Player) {
                if (!sender.isOp) {
                    if (args.size == 1) {
                        val commandList = listOf("menu")
                        val startArgs = args[0]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                } else {
                    if (args.size == 1) {
                        val commandList = listOf("booster", "create", "potions", "player", "playsound", "server", "sup", "menu", "enchant", "arena")
                        val startArgs = args[0]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                    if (args.size == 2) {
                        if (args[0].equals("arena", ignoreCase = true)) {
                            val commandList = listOf("createSpawnPoint", "remove")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("booster", ignoreCase = true)) {
                            val commandList = listOf("add", "remove")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("player", ignoreCase = true)) {
                            val commandList = listOf("check")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("playsound", ignoreCase = true)) {
                            val letters = args[1]
                            return SortingUtil.sortListWithContainsLetters(soundList, letters)
                        }
                        if (args[0].equals("server", ignoreCase = true)) {
                            val commandList = listOf("setspawn")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("sup", ignoreCase = true)) {
                            val commandList = listOf("sethealth")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("create", ignoreCase = true)) {
                            val commandList = listOf("sign", "kit", "potion")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("enchant", ignoreCase = true)) {
                            val commandList = listOf("add", "clear")
                            val startArgs = args[1]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }


                    }
                    if (args.size == 3) {
                        if (args[1].equals("sign", ignoreCase = true)) {
                            val commandList = listOf("work", "create", "update")
                            val startArgs = args[2]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("booster", ignoreCase = true)) {
                            val playersList = PlayersUtil.getOnlinePlayersName()
                            val startArgs = args[2]
                            return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                        }
                        if (args[1].equals("check", ignoreCase = true)) {
                            val playersList = PlayersUtil.getOnlinePlayersName()
                            val startArgs = args[2]

                            return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                        }
                        if (args[1].equals("sethealth", ignoreCase = true)) {
                            val playersList = PlayersUtil.getOnlinePlayersName()
                            val startArgs = args[2]

                            return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                        }
                        if (args[1].equals("kit", ignoreCase = true)) {
                            val commandList = listOf("get", "create", "update")
                            val startArgs = args[2]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[1].equals("potion", ignoreCase = true)) {
                            val commandList = listOf("get", "create", "update")
                            val startArgs = args[2]
                            return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                        }
                        if (args[0].equals("enchant", ignoreCase = true)) {
                            if (args[1].equals("add", ignoreCase = true)) {
                                val letters = args[2]
                                return SortingUtil.sortListWithContainsLetters(enchantmentList, letters)
                            }
                        }
                    }
                }
            } else {
                if (args.size == 1) {
                    val commandList = listOf("booster", "sign", "player", "playsound", "server", "sup")
                    val startArgs = args[0]
                    return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                }
                if (args.size == 2) {
                    if (args[0].equals("sign", ignoreCase = true)) {
                        val commandList = listOf("work", "create", "update")
                        val startArgs = args[1]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                    if (args[0].equals("booster", ignoreCase = true)) {
                        val commandList = listOf("add", "remove")
                        val startArgs = args[1]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                    if (args[0].equals("player", ignoreCase = true)) {
                        val commandList = listOf("check")
                        val startArgs = args[1]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                    if (args[0].equals("playsound", ignoreCase = true)) {
                        val soundsList: MutableList<String> = mutableListOf()
                        val letters = args[1]
                        Sound.values().forEach {
                            soundsList.add(it.toString())
                        }
                        return SortingUtil.sortListWithContainsLetters(soundsList, letters)
                    }
                    if (args[0].equals("server", ignoreCase = true)) {
                        val commandList = listOf("setspawn", "kit")
                        val startArgs = args[1]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                    if (args[0].equals("sup", ignoreCase = true)) {
                        val commandList = listOf("sethealth")
                        val startArgs = args[1]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                }
                if (args.size == 3) {
                    if (args[0].equals("booster", ignoreCase = true)) {
                        val playersList = PlayersUtil.getOnlinePlayersName()
                        val startArgs = args[2]

                        return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                    }
                    if (args[1].equals("check", ignoreCase = true)) {
                        val playersList = PlayersUtil.getOnlinePlayersName()
                        val startArgs = args[2]

                        return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                    }
                    if (args[1].equals("sethealth", ignoreCase = true)) {
                        val playersList = PlayersUtil.getOnlinePlayersName()
                        val startArgs = args[2]

                        return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                    }
                    if (args[1].equals("kit", ignoreCase = true)) {
                        val commandList = listOf("get", "create", "update")
                        val startArgs = args[2]
                        return SortingUtil.sortListWithStartLetters(commandList, startArgs)
                    }
                }
            }
        }
        return null
    }
}
