package ru.remsoftware.database

import ru.remsoftware.game.player.KitPlayer
import ru.remsoftware.game.signs.MoneySignData
import ru.tinkoff.kora.database.common.annotation.Query
import ru.tinkoff.kora.database.common.annotation.Repository
import ru.tinkoff.kora.database.jdbc.JdbcRepository
import java.util.*


@Repository
interface DataBaseRepository : JdbcRepository {
    // Query for players data
    @Query("select name, kit, money, donate_group, arena, kills, current_kills, deaths, local_booster, active_booster, booster_time FROM kitpvp.kit_players where name = :name")
    fun loadPlayerData(name: String): KitPlayer?

    @Query("update kitpvp.kit_players set name = :data.name, kit = :data.kit, money = :data.money, donate_group = :data.donateGroup, arena = :data.arena, kills = :data.kills, current_kills = :data.currentKills, deaths = :data.deaths, local_booster = :data.localBooster, active_booster = :data.activeBooster, booster_time = :data.boosterTime where name = :data.name")
    fun updatePlayer(data: KitPlayer)

    @Query("update kitpvp.kit_players set money = :money where name = :name")
    fun updateMoney(name: String, money: Int)

    @Query("insert into kitpvp.kit_players (name, money, donate_group, arena, kills, deaths, current_kills, kit, local_booster, active_booster, booster_time) values (:data.name, :data.money, :data.donateGroup, :data.arena, :data.kills, :data.deaths, :data.currentKills, :data.kit, :data.localBooster, :data.activeBooster, :data.boosterTime)")
    fun createPlayer(data: KitPlayer)

    // Query for signs data

    @Query("insert into kitpvp.money_signs (location, reward, status, cooldown, remaining_time) values (:data.location, :data.reward, :data.status, :data.cooldown, :data.remainingTime)")
    fun createSign(data: MoneySignData)

    @Query("select location, reward, status, cooldown, remaining_time from kitpvp.money_signs")
    fun loadSignData(): List<MoneySignData>

    @Query("update kitpvp.money_signs set location = :data.location, reward = :data.reward, status = :data.status, cooldown = :data.cooldown, remaining_time = :data.remainingTime where location = :data.location")
    fun updateSignData(data: MoneySignData)
}



