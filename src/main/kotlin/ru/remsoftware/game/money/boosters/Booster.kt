package ru.remsoftware.game.money.boosters

import org.bukkit.boss.BossBar
import kotlin.time.Duration

data class Booster(
    val activationTime: Long,
    val duration: Long,
    var remainingTime: Long,
    val local: Boolean,
    val activator: String,
)