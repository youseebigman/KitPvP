package ru.remsoftware.game.signs

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.LocationParser
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component
import java.util.*


@Component
class SignService(
    private val locParse: LocationParser,
) : Listener {

    private val moneySignsCache = hashMapOf<Location, MoneySignEntity>()
    val signWorkers = mutableListOf<String>()
    var selectedSign: Block? = null
    var signRestorer = mutableListOf<MoneySignEntity>()

    init {
        GlobalTaskContext.every(20, 20) {
            val iterator = signRestorer.listIterator()
            while (iterator.hasNext()) {
                val sign = iterator.next()
                val remainingTime = sign.remainingTime
                if (remainingTime > 0) {
                    sign.remainingTime -= 1
                } else {
                    sign.status = true
                    iterator.remove()
                }
            }
        }
    }

    fun get(loc: Location) = moneySignsCache[loc]

    fun set(loc: Location, data: MoneySignEntity) {
        moneySignsCache[loc] = data
    }

    fun all(): MutableCollection<MoneySignEntity> = Collections.unmodifiableCollection(moneySignsCache.values)

    fun getWorkers() = signWorkers

    fun setWorker(name: String) = signWorkers.add(signWorkers.size, name)

    fun invalidateWorker(name: String) = signWorkers.remove(name)

    fun selectSign(block: Block) {
        selectedSign = block
    }

    fun getSelectSign() = selectedSign

    fun restore(moneySign: MoneySignEntity) {
        signRestorer.add(moneySign)
    }

    fun moneySignsLoader(logger: Logger, database: DataBaseRepository) {
        val signsListLoader = SignsLoader(logger, database)
        val signsDataList = signsListLoader.moneySigns
        for (sing in signsDataList) {
            val loc = locParse.strToLoc(sing.location)
            val signsData = MoneySignEntity(loc, sing.reward, sing.status, sing.cooldown, sing.remainingTime)
            moneySignsCache[loc] = signsData
            if (!signsData.status) {
                restore(signsData)
            }
        }
    }

    fun createSign(database: DataBaseRepository, sign: Block, reward: Int, cooldown: Long, player: Player) {
        if (moneySignsCache.containsKey(sign.location)) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c&lУ этой таблички уже установлены данные, чтобы обновить их напишите /kitpvp sign update")
        } else {
            val dataLocation = locParse.locToStr(sign.location)
            val cooldownMillis = cooldown * 1000
            val moneySignData = MoneySignData(dataLocation, reward, true, cooldownMillis, 0)
            val moneySignEntity = MoneySignEntity(sign.location, reward, true, cooldownMillis, 0)
            moneySignsCache[sign.location] = moneySignEntity
            database.createSign(moneySignData)
            val signState = sign.state
            if (signState is Sign) {
                signState.setLine(1, "Забрать")
                signState.setLine(2, "Награду")
                signState.update()
            }
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a&L Вы успешно установили табличку")
        }
    }

    fun updateSign(database: DataBaseRepository, sign: Block, reward: Int, cooldown: Long, player: Player) {
        val dataLocation = locParse.locToStr(sign.location)
        val cooldownMillis = cooldown * 1000
        val moneySignData = MoneySignData(dataLocation, reward, true, cooldownMillis, 0)
        val moneySignEntity = MoneySignEntity(sign.location, reward, true, cooldownMillis, 0)
        moneySignsCache[sign.location] = moneySignEntity
        database.updateSignData(moneySignData)
        val signState = sign.state
        if (signState is Sign) {
            signState.setLine(1, "Забрать")
            signState.setLine(2, "Награду")
            signState.update()
        }
        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a&L Вы успешно обновили табличку")
    }


}
