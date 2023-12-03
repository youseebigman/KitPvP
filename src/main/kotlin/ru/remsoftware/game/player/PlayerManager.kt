package ru.remsoftware.game.player

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffectType
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.inventories.InventoryManager
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.server.ServerInfoService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.number.NumberUtil
import ru.starfarm.core.util.time.CooldownUtil
import ru.starfarm.core.util.time.Time
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PlayerManager(
    private val playerService: PlayerService,
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val moneyManager: MoneyManager,
    private val playerCombatManager: PlayerCombatManager,
    private val serverInfoService: ServerInfoService,
    private val inventoryManager: InventoryManager,
    private val arenaService: ArenaService,
) : Listener {


    fun handleKillStreakOnKill(victimName: String, killerName: String, kills: Int) {
        playerService.invalidatePlayerKillStreak(victimName)
        playerService.setPlayerKillStreak(killerName, kills)
        playerService.handleKillStreakBossBar()
    }

    fun handleKillStreakOnQuit(playerName: String) {
        if (serverInfoService.killStreakBossBar.first.equals(playerName)) {
            playerService.handleKillStreakBossBar()
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        savePlayerGameData(event.player)
        handleKillStreakOnQuit(event.player.name)
    }

    /*@EventHandler
    fun onPlayerKickEvent(event: PlayerKickEvent) {
        event.leaveMessage = null
        savePlayerGameData(event.player)
        handleKillStreakOnQuit(event.player.name)
    }*/

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null
        event.drops.clear()
        event.droppedExp = 0
        val killer = event.entity.killer
        val victim = event.entity.player
        val victimName = victim.name
        for (potionEffect in victim.activePotionEffects) {
            victim.removePotionEffect(potionEffect.type)
        }
        val ld = victim.lastDamageCause
        if (killer != null) {
            playerCombatManager.invalidateCombat(killer.name)
            playerCombatManager.invalidateCombat(victimName)
            handleStatsOnKill(killer, victim)
        } else {
            val victimData = playerService[victim]!!
            val moneyForKill = moneyManager.handleMoneyOnKill(victimData.money)
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.arena = "lobby"
            victimData.kit = "default"
            playerService.invalidatePlayerKillStreak(victimName)
            if (victimData.money >= 20) {
                moneyManager.removeMoneyBecauseDeath(victim.name, moneyForKill)
            }
            playerService[victimName] = victimData
            ChatUtil.sendMessage(victim, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$moneyForKill &fмонет из-за смерти")
            logger.log("$victimName умер от $ld")
        }
    }

    @EventHandler
    fun onPlayerGetDamage(event: EntityDamageEvent) {
        val player = event.entity
        val cause = event.cause
        if (player is Player) {
            if (player.world.name.equals("lobby")) {
                if (!arenaService.SPAWN_ARENA.contains(player)) {
                    event.isCancelled = true
                }
            }
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
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (player.location.y <= 0) {
            event.player.teleport(serverInfoService.spawn)
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = serverInfoService.serverInfo!!.spawn
        event.player.inventory.clear()
        inventoryManager.setDefaultInventory(event.player)
    }

    fun getBonus(player: Player) {
        val playerName = player.name
        if (CooldownUtil.has("bonus", player)) {
            val cooldown = NumberUtil.getTime(CooldownUtil.get("bonus", player))
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Бонус будет доступен через $cooldown")
            if (player.openInventory != null) {
                player.closeInventory()
            }
        } else {
            var bonus = 500
            val donatePermissions = playerService.getDonatePermissions(player.name)!!
            val bonusPermission = donatePermissions["bonus"]!!
            if (bonusPermission == 1) {
                CooldownUtil.put("bonus", player, Time(30, TimeUnit.MINUTES))
                moneyManager.addMoney(playerName, bonus)
                val playerBalance = playerService[playerName]!!.money
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a Вы получили $$bonus монет")
                player.playSound(player.eyeLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.5F)
                logger.log("Игрок $playerName получил бонус в размере $bonus монет. Текущий баланс: $playerBalance")
            } else {
                CooldownUtil.put("bonus", player, Time(30, TimeUnit.MINUTES))
                bonus *= bonusPermission
                moneyManager.addMoney(playerName, bonus)
                val playerBalance = playerService[playerName]!!.money
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a Вы получили $$bonus монет")
                player.playSound(player.eyeLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.5F)
                logger.log("Игрок $playerName получил бонус в размере $bonus монет. Текущий баланс: $playerBalance")
            }
        }
    }

    fun savePlayerGameData(player: Player) {
        val playerName = player.name
        if (playerCombatManager.isCombatPlayer(playerName)) {
            val killer = playerCombatManager.getLastDamager(playerName)!!
            playerCombatManager.invalidateCombat(killer)
            combatPlayersDataManageOnQuit(killer, playerName)
            playerService.savePlayerGameData(player)
            logger.log("Игрок $playerName вышел из игры во время боя и был убил игроком $killer")
            playerService.invalidatePlayerKillStreak(playerName)
            playerService.invalidate(playerName)
        } else {
            playerService.savePlayerGameData(player)
            playerService.invalidatePlayerKillStreak(playerName)
            playerService.invalidate(playerName)
            logger.log("Update players data for $playerName")
        }
    }

    fun combatPlayersDataManageOnQuit(killer: String, victim: String) {
        val killerData = playerService[killer]
        if (killerData == null) {
            val killerOfflineData = playerService.playerDataLoad(killer)
            val victimData = playerService[victim]!!
            val victimMoney = victimData.money
            val moneyForKill = moneyManager.handleMoneyOnKill(victimMoney)
            killerOfflineData.kills += 1
            killerOfflineData.currentKills += 1
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.kit = "default"
            victimData.arena = "lobby"
            moneyManager.addMoney(killer, moneyForKill)
            if (victimMoney <= 20) {
                victimData.money = 0
            } else {
                moneyManager.removeMoneyBecauseDeath(victim, moneyForKill)
            }
            database.updatePlayer(killerOfflineData)
            database.updatePlayer(victimData)
        } else {
            val victimData = playerService[victim]!!
            val moneyForKill = moneyManager.handleMoneyOnKill(victimData.money)
            killerData.kills += 1
            killerData.currentKills += 1
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.kit = "default"
            victimData.arena = "lobby"
            val victimMoney = victimData.money
            moneyManager.addMoney(killer, moneyForKill)
            if (victimMoney <= 20) {
                victimData.money = 0
            } else {
                moneyManager.removeMoneyBecauseDeath(victim, moneyForKill)
            }

            playerService[killer] = killerData
            playerService[victim] = victimData
            database.updatePlayer(killerData)
            database.updatePlayer(victimData)
            ChatUtil.sendMessage(Bukkit.getPlayer(killer), "&8[&b&lKit&4&lPvP&8]&f Ваш противник вышел из игры во время боя. Вы получили &a&l$moneyForKill &fмонет за его убийство")
        }
    }

    fun teleportOnRandomSpawnPoints(worldName: String, player: Player) {
        val kitPlayer = playerService[player.name]!!
        val locationList = arenaService[worldName]
        if (locationList != null) {
            val location = locationList.random()
            val teleportLocation = Location(location.world, location.x, location.y, location.z, location.yaw, location.pitch)
            teleportLocation.add(0.0, 0.3, 0.0)
            val chunk = location.chunk
            if (chunk.isLoaded) {
                player.teleport(teleportLocation)
            } else {
                println("loading chunk")
                teleportLocation.world.regenerateChunk(chunk.x, chunk.z)
                teleportLocation.world.loadChunk(chunk)
                if (!teleportLocation.chunk.isLoaded) {
                    println("Chunk not loaded")
                }
                player.teleport(teleportLocation)
            }
            kitPlayer.arena = worldName
            playerService[player.name] = kitPlayer
            player.playSound(player.eyeLocation, Sound.BLOCK_END_PORTAL_SPAWN, 0.6f, 1.0f)
        }
    }


    fun moveToOwnPosition(player: Player, pos: Location) {
        player.teleport(pos)
    }

    fun moveToSpawn(player: Player) {
        val kitPlayer = playerService.get(player)!!
        val spawn = serverInfoService.spawn!!
        val teleportLocation = Location(spawn.world, spawn.x, spawn.y, spawn.z, spawn.yaw, spawn.pitch)
        teleportLocation.add(0.0, 0.3, 0.0)
        if (player.isOp) {
            player.teleport(teleportLocation)
        } else if (kitPlayer.arena.equals("lobby") && !arenaService.SPAWN_ARENA.contains(player)) {
            player.teleport(teleportLocation)
        } else {
            if (playerCombatManager.isCombatPlayer(player.name)) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы не можете телепортироваться во время боя!")
            } else {
                val donateGroup = kitPlayer.donateGroup
                var teleportDuration: Int = when {
                    donateGroup == 1 -> 7
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
                        player.teleport(teleportLocation)
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
            if (victimMoney <= moneyForKill) {
                moneyManager.removeMoneyBecauseDeath(victimName, victimMoney)
            } else {
                moneyManager.removeMoneyBecauseDeath(victimName, moneyForKill)
            }
            playerService[victim.name] = victimData
            ChatUtil.sendMessage(victim, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$moneyForKill &fмонет из-за смерти")
            handleKillStreakOnKill(victimName, killerName, 0)
            logger.log("${victim.name} умер от себя")
        } else {
            killerData.currentKills += 1
            killerData.kills += 1
            if (victimData.currentKills >= 10) {
                val endStreakMessage = ChatUtil.format("&8[&b&lKit&4&lPvP&8]&d&l Игрок &f&l$killerName &d&lпрервал серию из &c&l${victimData.currentKills} убийств &d&lигрока &f&l$victimName!\n &d&lЗа что получил &a&l$$moneyForKill монет!")
                Bukkit.getOnlinePlayers().forEach {
                    it.sendMessage(endStreakMessage)
                }
            }
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.kit = "default"
            victimData.arena = "lobby"
            moneyManager.addMoney(killerName, moneyForKill)

            handleKillStreakOnKill(victimName, killerName, killerData.currentKills)
            if (victimMoney <= 20) {
                victimData.money = 0
                VariationMessages.sendMessageWithVariants(victimMoney, null, "death", victim, killer)
            } else {
                victimData.money -= moneyForKill
                VariationMessages.sendMessageWithVariants(moneyForKill, null, "death", victim, killer)
            }
            VariationMessages.sendMessageWithVariants(moneyForKill, null, "kill", victim, killer)

            if (killerData.currentKills % 10 == 0) {
                val streakMessage = ChatUtil.format("&8[&b&lKit&4&lPvP&8]&d&l Игрок &f&l$killerName &d&lсовершил уже &c&l${killerData.currentKills} убийств &d&lподряд! Кто же его остановит?")
                Bukkit.getOnlinePlayers().forEach {
                    it.sendMessage(streakMessage)
                }
            }

            playerService[killerName] = killerData
            playerService[victimName] = victimData
            database.updatePlayer(killerData)
            database.updatePlayer(victimData)
            logger.log("Игрок ${victim.name} был убит игроком ${killer.name}")
        }
    }
}