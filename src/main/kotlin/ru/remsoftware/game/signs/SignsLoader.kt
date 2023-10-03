package ru.remsoftware.game.signs

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger

class SignsLoader(
    private val logger: Logger,
    private val database: DataBaseRepository
) {

    var moneySigns: List<MoneySignData>
        private set

    init {
        val moneySignsData = database.loadSignData()
        this.moneySigns = moneySignsData

        logger.log("Signs have been uploaded in the amount of ${moneySigns.size} pieces")
    }
}