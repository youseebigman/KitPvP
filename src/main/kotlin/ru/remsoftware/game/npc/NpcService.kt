package ru.remsoftware.game.npc

import org.bukkit.event.Listener
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.menus.ArenasMenu
import ru.remsoftware.game.menus.KitsMenu
import ru.remsoftware.game.menus.MenuUtil
import ru.remsoftware.game.menus.ShopMenu
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.LocationParser
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.entity.PlayerInteractFakeEntityEvent
import ru.starfarm.core.entity.impl.FakeVillager
import ru.starfarm.core.event.on
import ru.starfarm.core.task.GlobalTaskContext
import ru.tinkoff.kora.common.Component

@Component
class NpcService(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
    private val locationParser: LocationParser,
    private val plugin: CorePlugin,
) : Listener {
    val Event by lazy(plugin::eventContext)
    private var teleportNpc: FakeVillager? = null
    private var kitNpc: FakeVillager? = null
    private var shopNpc: FakeVillager? = null
    private var donateNpc: FakeVillager? = null
    init {
        GlobalTaskContext.asyncAfter(20) {
            teleportNpc = FakeVillager(locationParser.strToLoc("world 495.5 90 -249.5 145 25")).apply {
                customName = "§a§lВыбрать арену"
                customNameVisible = true
            }
            kitNpc = FakeVillager(locationParser.strToLoc("world 490.5 90 -248.5 165 25")).apply {
                customName = "§4§lВыбрать кит"
                customNameVisible = true
            }
            shopNpc = FakeVillager(locationParser.strToLoc("world 484.5 90 -248.5 -165 25")).apply {
                customName = "§6§lКупить предметы"
                customNameVisible = true
            }
            donateNpc = FakeVillager(locationParser.strToLoc("world 479.5 90 -249.5 -145 25")).apply {
                customName = "§d§lДонат"
                customNameVisible = true
            }
            it.cancel()
        }

        Event.on<PlayerInteractFakeEntityEvent> {
            if (entity == teleportNpc) {
                ArenasMenu(kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
            }
            if (entity == kitNpc) {
                KitsMenu(kitService, kitManager, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
            }
            if (entity == shopNpc) {
                ShopMenu(kitManager, kitService, moneyManager, menuUtil, playerService, arenaService, inventoryParser).openInventory(player)
            }
        }
    }
}