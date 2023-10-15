package ru.remsoftware.utils

import org.bukkit.entity.Player
import ru.starfarm.core.util.format.ChatUtil

object VariationMessages {
    fun sendMessageWithVariants(divisible: Int, player: Player, reason: String) {
        if (reason.equals("death")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монет за смерть")
            } else {
                val remainder = divisible % 10

                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монет за смерть")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монету за смерть")
                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы потеряли $divisible монеты за смерть")
                }
            }
        }
        if (reason.equals("kill")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монет за убийство игрока")
            } else {
                val remainder = divisible % 10
                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монет за убийство игрока")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монету за убийство игрока")
                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $divisible монеты за убийство игрока")
                }
            }
        }
        if (reason.equals("cooldown")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунд")
            } else {
                val remainder = divisible % 10
                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунд")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунду")

                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунды")
                }
            }
        }
        if (reason.equals("potion_wait")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не можете выпить зелье! Подождите ещё $divisible секунд")
            } else {
                val remainder = divisible % 10
                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не можете выпить зелье! Подождите ещё $divisible секунд")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не можете выпить зелье! Подождите ещё $divisible секунду")

                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не можете выпить зелье! Подождите ещё $divisible секунды")
                }
            }
        }
    }
}