package ru.remsoftware.game.player

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffectType
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.inventories.InventoryManager
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class PlayerManager(
    private val playerService: PlayerService,
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val moneyManager: MoneyManager,
    private val playerCombatManager: PlayerCombatManager,
    private val serverInfoService: ServerInfoService,
    private val inventoryManager: InventoryManager,
) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val victim = player.name
        if (playerCombatManager.isCombatPlayer(victim)) {
            val killer = playerCombatManager.getLastDamager(victim)!!
            combatPlayersDataManageOnQuit(killer, victim)
            playerService.savePlayerGameData(player)
            logger.log("Игрок $victim вышел из игры во время боя и был убил игроком $killer")
            playerService.invalidate(victim)
        } else {
            playerService.savePlayerGameData(player)
            logger.log("Update players data for $victim")
            playerService.invalidate(victim)
        }
    }

    @EventHandler
    fun onPlayerKickEvent(event: PlayerKickEvent) {
        val player = event.player
        val victim = player.name
        if (playerCombatManager.isCombatPlayer(victim)) {
            val killer = playerCombatManager.getLastDamager(victim)!!
            combatPlayersDataManageOnQuit(killer, victim)
            playerService.savePlayerGameData(player)
            logger.log("Игрок $victim вышел из игры во время боя и был убил игроком $killer")
            playerService.invalidate(victim)
        } else {
            playerService.savePlayerGameData(player)
            logger.log("Update players data for $victim")
            playerService.invalidate(victim)
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null
        event.drops.clear()
        event.droppedExp = 0
        val killer = event.entity.killer
        val victim = event.entity.player
        for (potionEffect in victim.activePotionEffects) {
            victim.removePotionEffect(potionEffect.type)
        }
        val ld = victim.lastDamageCause
        if (killer != null) {
            handleStatsOnKill(killer, victim)
        } else {
            val victimData = playerService[victim]!!
            val moneyForKill = moneyManager.handleMoneyOnKill(victimData.money)
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.arena = "lobby"
            victimData.kit = "default"
            moneyManager.removeMoneyBecauseDeath(victim.name, moneyForKill)
            playerService[victim.name] = victimData
            ChatUtil.sendMessage(victim, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$moneyForKill &fмонет из-за смерти")
            logger.log("${victim.name} умер от $ld")
        }
    }

    @EventHandler
    fun onPlayerGetDamage(event: EntityDamageEvent) {
        val player = event.entity
        val cause = event.cause
        if (player is Player) {
            val kitPlayer = playerService[player]!!
            if (cause == (EntityDamageEvent.DamageCause.FALL)) {
                if (kitPlayer.kit == "Ангел") {
                    event.isCancelled = true
                }
            }
            if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                playerCombatManager.handleAbsorptionHP(player, event.damage)
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = serverInfoService.serverInfo!!.spawn
        event.player.inventory.clear()
        inventoryManager.setDefaultInventory(event.player)
    }

    fun combatPlayersDataManageOnQuit(killer: String, victim: String) {
        val killerData = playerService[killer]!!
        val victimData = playerService[victim]!!
        val moneyForKill = moneyManager.handleMoneyOnKill(victimData.money) * 2
        killerData.kills += 1
        killerData.currentKills += 1
        victimData.currentKills = 0
        victimData.deaths += 1
        victimData.kit = "default"
        victimData.arena = "lobby"
        moneyManager.addMoney(killer, moneyForKill)
        moneyManager.removeMoneyBecauseDeath(victim, moneyForKill)
        playerService[killer] = killerData
        playerService[victim] = victimData
        database.updatePlayer(killerData)
        database.updatePlayer(victimData)
        ChatUtil.sendMessage(Bukkit.getPlayer(killer), "&8[&b&lKit&4&lPvP&8]&f Ваш противник вышел из игры во время боя. Вы получили &a&l$moneyForKill &fмонет за его убийство")
    }


    fun moveToOwnPosition(player: Player, pos: Location) {
        player.teleport(pos)
    }

    fun moveToSpawn(player: Player) {
        val kitPlayer = playerService.get(player)!!
        val spawn = serverInfoService.serverInfo!!.spawn
        if (spawn != null && kitPlayer.arena.equals("lobby")) {
            player.teleport(spawn)
        } else if (spawn != null && !kitPlayer.arena.equals("lobby")) {
            if (playerCombatManager.isCombatPlayer(player.name)) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не можете телепортироваться во время боя!")
            } else {
                val donateGroup = kitPlayer.donateGroup
                println(donateGroup)
                var teleportDuration: Int = when {
                    donateGroup in 1..2 -> 7
                    donateGroup in 2..4 -> 6
                    donateGroup in 5..6 -> 5
                    donateGroup in 7..8 -> 4
                    donateGroup == 9 -> 3
                    else -> 8
                }
                val currentPlayerLocation = player.location
                val x = currentPlayerLocation.x.toInt()
                val z = currentPlayerLocation.z.toInt()
                val seconds = if (teleportDuration > 4) "секунд" else "секунды"
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&6 Вы будете телепортированы через $teleportDuration $seconds, не двигайтесь!")
                GlobalTaskContext.every(20, 20) {
                    val currentX = player.location.x.toInt()
                    val currentZ = player.location.z.toInt()
                    if (teleportDuration != 0) {
                        if (x != currentX || z != currentZ) {
                            println("$x, $z, $currentX. $currentZ")
                            teleportDuration = -1
                            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы сдвинулись с места, телепортация отменена!")
                            it.cancel()
                        } else {
                            teleportDuration--
                        }
                    }
                    if (teleportDuration in 1..3) {
                        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&6 Телепортация через $teleportDuration...")
                    }
                    if (teleportDuration == 0) {
                        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&6 Телепортация...")
                        player.teleport(spawn)
                        kitPlayer.arena = "lobby"
                        playerService[player.name] = kitPlayer
                        player.playSound(player.eyeLocation, Sound.BLOCK_PORTAL_TRAVEL, 1.0f, 1.0f)
                        it.cancel()
                    }
                }
            }
        }
    }

    private fun handleStatsOnKill(killer: Player, victim: Player) {
        val killerName = killer.name
        val victimName = victim.name
        val killerData = playerService[killerName]!!
        val victimData = playerService[victimName]!!
        val victimMoney = victimData.money
        val moneyForKill: Int = moneyManager.handleMoneyOnKill(victimData.money)

        if (victimName == killerName) {
            victimData.deaths += 1
            victimData.currentKills = 0
            victimData.kit = "default"
            moneyManager.removeMoneyBecauseDeath(victimName, moneyForKill)
            playerService[victim.name] = victimData
            ChatUtil.sendMessage(victim, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$moneyForKill &fмонет из-за смерти")
            logger.log("${victim.name} умер от себя")
        } else {
            killerData.currentKills += 1
            killerData.kills += 1
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.kit = "default"
            victimData.arena = "lobby"
            moneyManager.addMoney(killerName, moneyForKill)
            logger.log("Игрок $killerName получил $moneyForKill монет за убийство игрока $victimName")

            if (victimMoney <= 20) {
                victimData.money = 0
                VariationMessages.sendMessageWithVariants(victimMoney, null, "death", victim, killer)
            } else {
                victimData.money -= moneyForKill
                VariationMessages.sendMessageWithVariants(moneyForKill, null, "death", victim, killer)
            }
            VariationMessages.sendMessageWithVariants(moneyForKill, null, "kill", victim, killer)
            if (victimData.money < 0) victimData.money = 0
            playerService[killerName] = killerData
            playerService[victimName] = victimData
            database.updatePlayer(killerData)
            database.updatePlayer(victimData)
            logger.log("Player ${victim.name} was killed by ${killer.name}")

        }
    }
}