package ru.remsoftware.game.kits

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component
import java.util.*

@Component
class KitService(
    private val dataBaseRepository: DataBaseRepository,
    private val logger: Logger,
) {
    private val kitCacheList = hashMapOf<String, KitData>()

    operator fun get(name: String) = kitCacheList[name]

    operator fun set(name: String, data: KitData) {
        kitCacheList[name] = data
    }
    fun all(): MutableCollection<KitData> = Collections.unmodifiableCollection(kitCacheList.values)

    fun kitsLoader(database: DataBaseRepository, logger: Logger) {
        val kitsLoader = KitDataLoader(database, logger)
        val kitList = kitsLoader.kits
        for (kit in kitList) {
            val kitData = KitData(kit.name, kit.icon, kit.inventory, kit.potionEffects, kit.price)
            kitCacheList[kit.name] = kitData
        }
    }
    fun createKit(kitData: KitData, database: DataBaseRepository) {
        database.createKit(kitData)
        set(kitData.name, kitData)
    }
    fun updateKit(kitData: KitData, database: DataBaseRepository) {
        database.updateKitData(kitData)
        set(kitData.name, kitData)
    }

}