package ru.remsoftware.game.potions

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger

class PotionDataLoader(
    private val dataBaseRepository: DataBaseRepository,
    private val logger: Logger,
) {
    var potions: List<PotionData>
        private set

    init {
        val potionsData = dataBaseRepository.loadPotions()
        this.potions = potionsData
        logger.log("Potions have been uploaded in the amount of ${potions.size} pieces")

    }
}