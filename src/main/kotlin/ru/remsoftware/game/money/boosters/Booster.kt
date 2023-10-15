package ru.remsoftware.game.money.boosters

data class Booster(
    val activationTime: Long,
    val duration: Int,
    var remainingTime: Int,
    val local: Boolean,
    val activator: String,
)