package ru.remsoftware.utils

import org.bukkit.entity.Player
import ru.starfarm.core.util.format.ChatUtil

object VariationMessages {

    fun returnMoneyVariants(money: Int): String {
        return if (money in 11..19) {
            "монет"
        } else {
            val remainder = money % 10
            if (remainder == 0 || remainder > 4) "монет"
            else if (remainder == 1) "монета"
            else "монеты"
        }
    }
    fun returnKillsVariants(kills: Int): String {
        return if (kills in 11..19) {
            "убийств"
        } else {
            val remainder = kills % 10
            if (remainder == 0 || remainder > 4) "убийств"
            else if (remainder == 1) "убийство"
            else "убийства"
        }
    }

    fun sendMessageWithVariants(divisible: Int, player: Player?, reason: String, victim: Player?, killer: Player?) {
        if (reason.equals("death")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(victim!!, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$$divisible &fмонет за смерть от игрока &b${killer!!.name}")
            } else {
                val remainder = divisible % 10

                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(victim!!, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$$divisible &fмонет за смерть от игрока &b${killer!!.name}")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(victim!!, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$$divisible &fмонету за смерть от игрока &b${killer!!.name}")
                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(victim!!, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$$divisible &fмонеты за смерть от игрока &b${killer!!.name}")
                }
            }
        }
        if (reason.equals("kill")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(killer!!, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a$$divisible &fмонет за убийство игрока &b${victim!!.name}")
            } else {
                val remainder = divisible % 10
                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(killer!!, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a$$divisible &fмонет за убийство игрока &b${victim!!.name}")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(killer!!, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a$$divisible &fмонету за убийство игрока &b${victim!!.name}")
                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(killer!!, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a$$divisible &fмонеты за убийство игрока &b${victim!!.name}")
                }
            }
        }
        if (reason.equals("cooldown")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунд")
            } else {
                val remainder = divisible % 10
                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунд")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунду")

                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через $divisible секунды")
                }
            }
        }
        if (reason.equals("potion_wait")) {
            if (divisible in 11..19) {
                ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы не можете использовать это зелье! Подождите ещё $divisible секунд")
            } else {
                val remainder = divisible % 10
                if (remainder == 0 || remainder > 4) {
                    ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы не можете использовать это зелье! Подождите ещё $divisible секунд")
                }
                if (remainder == 1) {
                    ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы не можете использовать это зелье! Подождите ещё $divisible секунду")

                }
                if (remainder in 2..4) {
                    ChatUtil.sendMessage(player!!, "&8[&b&lKit&4&lPvP&8]&c Вы не можете использовать это зелье! Подождите ещё $divisible секунды")
                }
            }
        }
    }
}