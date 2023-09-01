package ru.remsoftware.database

import ru.remsoftware.game.player.KitPlayer
import ru.remsoftware.game.signs.MoneySignData
import ru.remsoftware.server.ServerInfoData
import ru.tinkoff.kora.database.common.annotation.Query
import ru.tinkoff.kora.database.common.annotation.Repository
import ru.tinkoff.kora.database.jdbc.JdbcRepository


@Repository
interface DataBaseRepository : JdbcRepository {
    // Query for players data
    @Query("select name, game_data, kit, money, donate_group, arena, kills, current_kills, deaths, local_booster, active_booster, booster_time, position, inventory, potion_effects FROM kitpvp.kit_players where name = :name")
    fun loadPlayerData(name: String): KitPlayer?

    @Query("update kitpvp.kit_players set name = :data.name, game_data = :data.gameData, kit = :data.kit, money = :data.money, donate_group = :data.donateGroup, arena = :data.arena, kills = :data.kills, current_kills = :data.currentKills, deaths = :data.deaths, local_booster = :data.localBooster, active_booster = :data.activeBooster, booster_time = :data.boosterTime, position = :data.position, inventory = :data.inventory, potion_effects = :data.potionEffects where name = :data.name")
    fun updatePlayer(data: KitPlayer)

    @Query("update kitpvp.kit_players set money = :money where name = :name")
    fun updateMoney(name: String, money: Int)

    @Query("insert into kitpvp.kit_players (name, money, donate_group, arena, kills, deaths, current_kills, kit, local_booster, active_booster, booster_time) values (:data.name, :data.money, :data.donateGroup, :data.arena, :data.kills, :data.deaths, :data.currentKills, :data.kit, :data.localBooster, :data.activeBooster, :data.boosterTime)")
    fun createPlayer(data: KitPlayer)

    @Query("update kitpvp.kit_players set position = :pos where name = :name")
    fun updatePosition(name: String, pos: String)

    @Query("update kitpvp.kit_players set inventory = :inventory where name = :name")
    fun updateInventory(name: String, inventory: String)

    // Query for signs data

    @Query("insert into kitpvp.money_signs (location, reward, status, cooldown, remaining_time) values (:data.location, :data.reward, :data.status, :data.cooldown, :data.remainingTime)")
    fun createSign(data: MoneySignData)

    @Query("select location, reward, status, cooldown, remaining_time from kitpvp.money_signs")
    fun loadSignData(): List<MoneySignData>

    @Query("update kitpvp.money_signs set location = :data.location, reward = :data.reward, status = :data.status, cooldown = :data.cooldown, remaining_time = :data.remainingTime where location = :data.location")
    fun updateSignData(data: MoneySignData)

    //Query for server data

    @Query("insert into kitpvp.server_data (world, spawn, global_booster) values(:world, :data.spawn, :data.globalBooster)")
    fun createServerData(world: String, data: ServerInfoData)
    @Query("select spawn, global_booster from kitpvp.server_data where world = :world")
    fun loadServerInfo(world: String): ServerInfoData?

    @Query("update kitpvp.server_data set spawn = :data.spawn, global_booster = :data.globalBooster where world = :world")
    fun updateServerData(world: String, data: ServerInfoData)
    @Query("update kitpvp.server_data set spawn = :location where world = :world")
    fun updateSpawn(world: String, location: String)
}



