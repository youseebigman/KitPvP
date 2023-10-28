package ru.remsoftware.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerManager
import ru.remsoftware.game.player.PlayerService
import ru.tinkoff.kora.common.Component

@Component
class SpawnCommand(
    private val playerManager: PlayerManager,
): TabExecutor {
    override fun onTabComplete(sender: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun onCommand(sender: CommandSender?, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            if (label.equals("spawn", ignoreCase = true)) {
                playerManager.moveToSpawn(sender)
            }
        }
        return true
    }
}