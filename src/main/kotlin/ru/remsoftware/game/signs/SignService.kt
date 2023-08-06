package ru.remsoftware.game.signs

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.utils.Logger
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component


@Component
class SignService(
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val moneyManager: MoneyManager,
    private val plugin: CorePlugin,

    ) : Listener {

    private val moneySignsCache = hashMapOf<Location, MoneySignEntity>()
    private val signWorkers = mutableListOf<String>()
    private var selectedSign: Block? = null
    private var signRestorer = mutableListOf<MoneySignEntity>()

    init {
        val mutableListIterator = signRestorer.listIterator()
        GlobalTaskContext.every(20, 20) {
            if (signRestorer.isNotEmpty()) {
                for (sign in signRestorer) {
                    val signRemainingTime = sign.remainingTime - 1
                    sign.remainingTime = signRemainingTime
                    println("$signRestorer")
                    if (sign.remainingTime <= 0) {
                        signRestorer.remove(sign)
                        sign.status = true
                        it.cancel()
                    }
                }
            }
        }
    }

    fun get(loc: Location) = moneySignsCache[loc]

    fun set(loc: Location, data: MoneySignEntity) {
        moneySignsCache[loc] = data
    }

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
    fun onPluginEnabled(event: PluginEnableEvent) {
        moneySignsLoader(logger, database)
    }
    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        for (sign in moneySignsCache) {
            val signEntity = sign.value
            val signData = MoneySignData(locToStr(signEntity.location), signEntity.reward, signEntity.status, signEntity.cooldown, signEntity.remainingTime)
            database.updateSignData(signData)
        }
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
                        val remainder = moneySign.remainingTime % 10
                        if (remainder == 0L || remainder > 4) {
                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через ${moneySign.remainingTime} секунд")
                        }
                        if (remainder == 1L) {
                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через ${moneySign.remainingTime} секунду")
                        }
                        if (remainder in 2L..4L) {
                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сможете забрать награду через ${moneySign.remainingTime} секунды")
                        }
                    }
                }
            }
        }
    }



        fun createSign(database: DataBaseRepository, sign: Block, reward: Int, cooldown: Long, player: Player) {
            if (moneySignsCache.containsKey(sign.location)) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c&lУ этой таблички уже установлены данные, вы можете их обновить")
            } else {
                val dataLocation = locToStr(sign.location)
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
            }
        }
    fun updateSign(database: DataBaseRepository, sign: Block, reward: Int, cooldown: Long, player: Player) {
        val dataLocation = locToStr(sign.location)
        val cooldownMillis = cooldown * 1000
        val moneySignData = MoneySignData(dataLocation, reward, true, cooldownMillis, 0)
        val moneySignEntity = MoneySignEntity(sign.location, reward, true, cooldownMillis, 0)
        moneySignsCache[sign.location] = moneySignEntity
        database.updateSignData(moneySignData)
    }

        fun moneySignsLoader(logger: Logger, database: DataBaseRepository) {
            val signsListLoader = SignsLoader(logger, database)
            val signsDataList = signsListLoader.moneySigns
            for (sing in signsDataList) {
                val loc = locStrToLoc(sing.location)
                val signsData = MoneySignEntity(loc, sing.reward, sing.status, sing.cooldown, sing.remainingTime)
                moneySignsCache[loc] = signsData
                if (!signsData.status) {
                    restore(signsData)
                }
            }
        }

        fun locToStr(loc: Location): String = "${loc.world.name}:${loc.blockX}:${loc.blockY}:${loc.blockZ}"

        fun locStrToLoc(loc: String): Location {
            val parts: List<String> = loc.split(":")
            val world: World = Bukkit.getServer().getWorld(parts[0])
            val x: Double = parts[1].toDouble()
            val y: Double = parts[2].toDouble()
            val z: Double = parts[3].toDouble()
            return Location(world, x, y, z)
        }


    }
