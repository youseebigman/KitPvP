package ru.remsoftware.game.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import ru.starfarm.core.profile.IProfileService
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class ChatListener : Listener {
    @EventHandler
    fun sendMessage(event: AsyncPlayerChatEvent) {
        event.isCancelled = true
        val playerProfile = IProfileService.get().getProfile(event.player)!!
        if (playerProfile.isActiveMute()) return
        val guildName = playerProfile.guildName
        val format = if (guildName == null) ChatUtil.format(" ${playerProfile.coloredNameWithTitle}§f: ${event.message}")
        else ChatUtil.format("§b<$guildName> ${playerProfile.coloredNameWithTitle}§f: ${event.message}")
        Bukkit.getOnlinePlayers().forEach { it.sendMessage(format) }
        Bukkit.getConsoleSender().sendMessage(format)
    }
}