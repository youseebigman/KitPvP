package ru.remsoftware.game.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
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
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null
        val player = event.player
        val kitPlayer = playerService[player]!!
        val gameData = kitPlayer.gameData
        val potionEffects = kitPlayer.potionEffects
        val position = kitPlayer.position
        val kitInv = kitPlayer.inventory
        val playerProfile = ApiManager.getPlayerProfile(player)
        if (playerProfile != null) {
            val donateGroup = playerProfile.donateGroup.weight
            kitPlayer.donateGroup = donateGroup
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
            for (i in 0..40) {
                val item: ItemStack? = inventory.getItem(i)
                if (item == null) {
                    player.inventory.setItem(i, ItemStack(Material.AIR))
                } else {
                    player.inventory.setItem(i, item)
                }
            }
        }
        if (kitPlayer.activeBooster) {
            boosterManager.createBooster(kitPlayer.boosterTime, true, player.name)
        }
        playerScoreboard.loadScoreboard(player)

    }

}