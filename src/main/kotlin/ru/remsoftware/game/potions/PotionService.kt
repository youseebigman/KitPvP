package ru.remsoftware.game.potions

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component
import java.util.*

@Component
class PotionService {

    private val cachePotions = hashMapOf<String, PotionData>()
    private val potionNameList = arrayListOf<String>()
    operator fun get(name: String) = cachePotions[name]

    operator fun set(name: String, data: PotionData) {
        cachePotions[name] = data
    }
    fun all(): MutableCollection<PotionData> = Collections.unmodifiableCollection(cachePotions.values)

    fun potionDataLoad(dataBaseRepository: DataBaseRepository, logger: Logger) {
        val potionDataList = PotionDataLoader(dataBaseRepository, logger).potions
        for (potionData in potionDataList) {
            set(potionData.name, potionData)
            potionNameList.add(potionData.name)
        }
    }
    fun createPotion(potionData: PotionData, dataBaseRepository: DataBaseRepository) {
        dataBaseRepository.createPotion(potionData)
        set(potionData.name, potionData)
    }
    fun updatePotion(potionData: PotionData, dataBaseRepository: DataBaseRepository) {
        dataBaseRepository.updatePotion(potionData)
        set(potionData.name, potionData)
    }
    fun getAllPotionsName(): MutableCollection<String> = Collections.unmodifiableCollection(potionNameList)

}