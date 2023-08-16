package ru.remsoftware.utils

import org.bukkit.Bukkit

object PlayersUtil {
    fun getOnlinePlayersName() : MutableList<String> {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        val playersList: MutableList<String> = mutableListOf()
        for (player in onlinePlayers) {
            playersList.add(player.name)
        }
        return playersList
    }
}