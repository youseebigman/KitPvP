package ru.remsoftware.game.kits

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component
import java.util.*

@Component
class KitService {
    private val cacheKitMap = hashMapOf<String, KitData>()

    val freeKits = hashMapOf<String, KitData>()
    val cheapKitsMap = hashMapOf<Int, KitData>()
    val averageKitsMap = hashMapOf<Int, KitData>()
    val bestKitsMap = hashMapOf<Int, KitData>()


    operator fun get(name: String) = cacheKitMap[name]

    operator fun set(name: String, data: KitData) {
        cacheKitMap[name] = data
    }
    fun all(): MutableCollection<KitData> = Collections.unmodifiableCollection(cacheKitMap.values)

    fun kitsLoader(database: DataBaseRepository, logger: Logger) {
        val kitsLoader = KitDataLoader(database, logger)
        val kitList = kitsLoader.kits
        for (kit in kitList) {
            val kitData = KitData(kit.name, kit.icon, kit.inventory, kit.potionEffects, kit.price, kit.donateCooldown, kit.donateGroup)
            cacheKitMap[kit.name] = kitData
            sortKitsByPrice(kitData)
        }
    }

    fun createKit(kitData: KitData, database: DataBaseRepository) {
        database.createKit(kitData)
        set(kitData.name, kitData)
        sortKitsByPrice(kitData)

    }

    fun updateKit(kitData: KitData, database: DataBaseRepository) {
        database.updateKitData(kitData)
        set(kitData.name, kitData)
        sortKitsByPrice(kitData)
    }

    fun sortKitsByPrice(kitData: KitData) {
        when (kitData.price) {
            0 -> {
                freeKits[kitData.name] = kitData
            }
            in 1..5000 -> {
                cheapKitsMap[kitData.price] = kitData
            }
            in 5001..50000 -> {
                averageKitsMap[kitData.price] = kitData
            }
            in 50001..10000000 -> {
                bestKitsMap[kitData.price] = kitData
            }
        }
    }

}