package ru.remsoftware.game.signs

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.utils.LocationParser
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component
import java.util.*


@Component
class SignService(
    private val moneyManager: MoneyManager,
    private val locParse: LocationParser,
) : Listener {

    val moneySignsCache = hashMapOf<Location, MoneySignEntity>()
    private val signWorkers = mutableListOf<String>()
    private var selectedSign: Block? = null
    private var signRestorer = mutableListOf<MoneySignEntity>()

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

    @EventHandler
    fun onPlayerClickOnSign(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock
        if (event.clickedBlock == null || event.clickedBlock.type.equals(Material.AIR)) {
            return
        }
        if (block.type.equals(Material.SIGN) || block.type.equals(Material.SIGN_POST) || block.type.equals(Material.WALL_SIGN)) {
            if (signWorkers.contains(player.name)) {
                selectSign(block)
                ChatUtil.sendMessage(player, "&aВы успешно выбрали табличку, можете присвоить ей данные")
            } else {
                val moneySign = get(block.location)
                if (moneySign == null) {
                    return
                } else {
                    if (moneySign.status) {
                        moneySign.status = false
                        moneySign.remainingTime = moneySign.cooldown / 1000
                        restore(moneySign)
                        moneyManager.addMoney(player.name, moneySign.reward, player)
                    } else {
                        VariationMessages.sendMessageWithVariants(moneySign.remainingTime.toInt(), player, "cooldown")
                    }
                }
            }
        }
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
