package ru.remsoftware.game.kits

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class KitService {
    private val cacheKitMap = hashMapOf<String, KitData>()

    val freeKits = hashMapOf<String, KitData>()
    val cheapKitsMap = hashMapOf<String, KitData>()
    val averageKitsMap = hashMapOf<String, KitData>()
    val bestKitsMap = hashMapOf<String, KitData>()


    operator fun get(name: String) = cacheKitMap[name]

    operator fun set(name: String, data: KitData) {
        cacheKitMap[name] = data
    }
    fun invalidate(name: String, map: HashMap<String, KitData>) = map.remove(name)


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
                if (freeKits.containsKey(kitData.name)) invalidate(kitData.name, freeKits)
                freeKits[kitData.name] = kitData
            }
            in 1..5000 -> {
                if (cheapKitsMap.containsKey(kitData.name)) invalidate(kitData.name, cheapKitsMap)
                cheapKitsMap[kitData.name] = kitData
            }
            in 5001..50000 -> {
                if (averageKitsMap.containsKey(kitData.name)) invalidate(kitData.name, averageKitsMap)
                averageKitsMap[kitData.name] = kitData
            }
            in 50001..100000000 -> {
                if (bestKitsMap.containsKey(kitData.name)) invalidate(kitData.name, bestKitsMap)
                bestKitsMap[kitData.name] = kitData
            }
        }
    }

}