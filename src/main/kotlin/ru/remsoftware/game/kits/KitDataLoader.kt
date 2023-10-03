package ru.remsoftware.game.kits

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger

class KitDataLoader(
    private val dataBaseRepository: DataBaseRepository,
    private val logger: Logger,
) {
    var kits: List<KitData>
        private set
    init {
        var kitData = dataBaseRepository.loadKitData()
        if (kitData == null) kitData = emptyList()
        this.kits = kitData
        logger.log("Kits have been uploaded in the amount of ${kits.size} pieces")
    }
}