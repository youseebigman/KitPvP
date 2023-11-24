package ru.remsoftware.game.kits

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class KitService(
    private val database: DataBaseRepository,
) {
    private val cacheKitMap = hashMapOf<String, KitData>()

    val freeKits = hashMapOf<String, KitData>()
    val cheapKitsMap = hashMapOf<String, KitData>()
    val averageKitsMap = hashMapOf<String, KitData>()
    val bestKitsMap = hashMapOf<String, KitData>()
    val donateKitsMap = hashMapOf<String, KitData>()
    operator fun get(name: String) = cacheKitMap[name]

    operator fun set(name: String, data: KitData) {
        cacheKitMap[name] = data
    }

    fun invalidate(name: String, map: HashMap<String, KitData>) = map.remove(name)


    fun all(): MutableCollection<KitData> = Collections.unmodifiableCollection(cacheKitMap.values)

    fun kitsLoader(logger: Logger) {
        val kitsLoader = KitDataLoader(database, logger)
        val kitList = kitsLoader.kits
        for (kit in kitList) {
            val kitData = KitData(kit.name, kit.icon, kit.inventory, kit.potionEffects, kit.price,  kit.donateGroup, kit.numberOfPurchases)
            cacheKitMap[kit.name] = kitData
            sortKits(kitData)
        }
    }

    fun createKit(kitData: KitData) {
        database.createKit(kitData)
        set(kitData.name, kitData)
        sortKits(kitData)

    }

    fun updateKit(kitData: KitData) {
        set(kitData.name, kitData)
        sortKits(kitData)
        database.updateKitData(kitData)
    }
    fun updateKitPurchases(name: String, amount: Int) {
        database.updateKitPurchases(name, amount)
    }

    fun sortKits(kitData: KitData) {
        when (kitData.price) {
            0 -> {
                freeKits[kitData.name] = kitData
            }
            in 1..4999 -> {
                cheapKitsMap[kitData.name] = kitData
            }
            in 5000..50000 -> {
                if (kitData.donateGroup != null) {
                    donateKitsMap[kitData.name] = kitData
                } else {
                    averageKitsMap[kitData.name] = kitData
                }
            }
            in 50001..100000000 -> {
                if (kitData.donateGroup != null) {
                    donateKitsMap[kitData.name] = kitData
                } else {
                    bestKitsMap[kitData.name] = kitData
                }
            }
        }
    }

}