package ru.remsoftware.database

import ru.remsoftware.game.arena.ArenaInfo
import ru.remsoftware.game.kits.KitData
import ru.remsoftware.game.player.KitPlayer
import ru.remsoftware.game.potions.PotionData
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.server.ServerInfoData
import ru.tinkoff.kora.database.common.annotation.Query
import ru.tinkoff.kora.database.common.annotation.Repository
import ru.tinkoff.kora.database.jdbc.JdbcRepository


@Repository
interface DataBaseRepository : JdbcRepository {
    // Query for players data
    @Query("select name, game_data, kit, money, donate_group, arena, kills, current_kills, deaths, local_booster, active_booster, booster_time, position, inventory, potion_effects, available_kits FROM kitpvp.kit_players where name = :name")
    fun loadPlayerData(name: String): KitPlayer?

    @Query("update kitpvp.kit_players set name = :data.name, game_data = :data.gameData, kit = :data.kit, money = :data.money, donate_group = :data.donateGroup, arena = :data.arena, kills = :data.kills, current_kills = :data.currentKills, deaths = :data.deaths, local_booster = :data.localBooster, active_booster = :data.activeBooster, booster_time = :data.boosterTime, position = :data.position, inventory = :data.inventory, potion_effects = :data.potionEffects, available_kits = :data.availableKits where name = :data.name")
    fun updatePlayer(data: KitPlayer)

    @Query("update kitpvp.kit_players set money = :money where name = :name")
    fun updateMoney(name: String, money: Int)

    @Query("insert into kitpvp.kit_players (name, money, donate_group, arena, kills, deaths, current_kills, kit, local_booster, active_booster, booster_time) values (:data.name, :data.money, :data.donateGroup, :data.arena, :data.kills, :data.deaths, :data.currentKills, :data.kit, :data.localBooster, :data.activeBooster, :data.boosterTime)")
    fun createPlayer(data: KitPlayer)

    //Query for kits data
    @Query("insert into kitpvp.kits_data (name, inventory, potion_effects, icon, price) values (:data.name, :data.inventory, :data.potionEffects, :data.icon, :data.price)")
    fun createKit(data: KitData)

    @Query("select name, inventory, potion_effects, icon, price, donate_group from kitpvp.kits_data")
    fun loadKitData(): List<KitData>?

    @Query("update kitpvp.kits_data set name = :data.name, inventory = :data.inventory, potion_effects = :data.potionEffects, icon = :data.icon, price = :data.price where name = :data.name")
    fun updateKitData(data: KitData)

    @Query("delete from kitpvp.kits_data where name = :name")
    fun removeKitData(name: String)

    // Query for signs data

    @Query("insert into kitpvp.money_signs (location, reward, status, cooldown, remaining_time) values (:data.location, :data.reward, :data.status, :data.cooldown, :data.remainingTime)")
    fun createSign(data: MoneySignData)

    @Query("select location, reward, status, cooldown, remaining_time from kitpvp.money_signs")
    fun loadSignData(): List<MoneySignData>

    @Query("update kitpvp.money_signs set location = :data.location, reward = :data.reward, status = :data.status, cooldown = :data.cooldown, remaining_time = :data.remainingTime where location = :data.location")
    fun updateSignData(data: MoneySignData)

    //Query for potions
    @Query("select * from kitpvp.kit_potions")
    fun loadPotions(): List<PotionData>

    @Query("insert into kitpvp.kit_potions (name, cooldown, potion) values (:data.name, :data.cooldown, :data.potion)")
    fun createPotion(data: PotionData)

    @Query("update kitpvp.kit_potions set cooldown = :data.cooldown, potion = :data.potion where name = :data.name")
    fun updatePotion(data: PotionData)

    //Query for server data

    @Query("insert into kitpvp.server_data (spawn, global_booster) values(:data.spawn, :data.globalBooster)")
    fun createServerData(data: ServerInfoData)

    @Query("select spawn, global_booster from kitpvp.server_data")
    fun loadServerInfo(): ServerInfoData?

    @Query("update kitpvp.server_data set spawn = :data.spawn, global_booster = :data.globalBooster")
    fun updateServerData(data: ServerInfoData)

    @Query("update kitpvp.server_data set spawn = :location")
    fun updateSpawn(location: String)

    // Query for arenas

    @Query("select name, spawn_points from kitpvp.arenas_data where name = :name")
    fun loadArenaLocations(name: String): ArenaInfo?

    @Query("update kitpvp.arenas_data set name = :name, spawn_points = :data where name = :name")
    fun updateArenaLocations(name: String, data: String)

    @Query("insert into kitpvp.arenas_data (name, spawn_points) values(:name, :data)")
    fun createArenaLocation(name: String, data: String)
}



