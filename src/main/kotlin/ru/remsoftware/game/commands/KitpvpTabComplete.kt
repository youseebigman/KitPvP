package ru.remsoftware.game.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import ru.remsoftware.utils.PlayersUtil
import ru.remsoftware.utils.SortingUtil
import ru.tinkoff.kora.common.Component

@Component
class KitpvpTabComplete : TabCompleter {
    override fun onTabComplete(sender: CommandSender?, command: Command, alias: String?, args: Array<out String>): MutableList<String>? {
        if (command.name.lowercase().equals("kitpvp")) {
            if (args.size == 1) {
                val commandList = listOf("booster", "sign", "player", "playsound")
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
                    return SortingUtil.sortListWithStartLetters(soundsList, letters)
                }
            }
            if (args.size == 3) {
                if (args[0].equals("booster", ignoreCase = true)) {
                    val playersList = PlayersUtil.getOnlinePlayersName()
                    val startArgs = args[2]

                    return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                }
                if (args[0].equals("player", ignoreCase = true)) {
                    val playersList = PlayersUtil.getOnlinePlayersName()
                    val startArgs = args[2]

                    return SortingUtil.sortListWithStartLetters(playersList, startArgs)
                }
            }
        }
        return null
    }
}
