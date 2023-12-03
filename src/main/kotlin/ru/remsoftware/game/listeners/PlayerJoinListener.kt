package ru.remsoftware.game.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.remsoftware.game.inventories.InventoryManager
import ru.remsoftware.game.money.boosters.BoosterManager
import ru.remsoftware.game.player.PlayerManager
import ru.remsoftware.game.player.PlayerScoreboard
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.GameDataParser
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.LocationParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.CorePlugin
import ru.tinkoff.kora.common.Component

@Component
class PlayerJoinListener(
    private val playerService: PlayerService,
    private val boosterManager: BoosterManager,
    private val playerScoreboard: PlayerScoreboard,
    private val locationParser: LocationParser,
    private val inventoryParser: InventoryParser,
    private val potionEffectParser: PotionEffectParser,
    private val gameDataParser: GameDataParser,
    private val inventoryManager: InventoryManager,
    private val playerManager: PlayerManager,
    private val plugin: CorePlugin,
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null
        val player = event.player
        val playerData = playerService[player]!!
        val gameData = playerData.gameData
        val potionEffects = playerData.potionEffects
        val position = playerData.position
        val kitInv = playerData.inventory
        val playerProfile = ApiManager.getPlayerProfile(player)
        if (playerProfile != null) {
            val donateGroup = playerProfile.donateGroup.weight
            playerData.donateGroup = donateGroup
        }
        if (position == null) {
            playerManager.moveToSpawn(player)
        } else {
            val pos = locationParser.strToLoc(position)
            playerManager.moveToOwnPosition(player, pos)
        }
        if (potionEffects != null) {
            val effects = potionEffectParser.jsonToPotionEffect(potionEffects)
            for (effect in effects) {
                player.addPotionEffect(effect)
            }
        }
        if (gameData != null) {
            gameDataParser.jsonToGameData(gameData, player)
        }
        if (kitInv == null) {
            inventoryManager.setDefaultInventory(player)
        } else {
            val inventory = inventoryParser.jsonToInventory(kitInv)
            for (i in inventory.withIndex()) {
                if (i.value == null) {
                    continue
                } else {
                    player.inventory.setItem(i.index, i.value)
                }
            }
        }
        if (playerData.activeBooster) {
            boosterManager.createBooster(playerData.boosterTime, true, player.name)
        }
        playerService.handleKillStreakBossBar()
        playerScoreboard.loadScoreboard(player)
        if (!player.world.name.equals("world")) {
            for (players in player.world.players) {
                player.hidePlayer(plugin, players)
                player.showPlayer(plugin, players)
            }
            for (players in player.world.players) {
                players.hidePlayer(plugin, player)
                players.showPlayer(plugin, player)
            }
        }

    }

}