package ru.remsoftware.game.player

import org.bukkit.entity.Player
import org.bukkit.event.Listener
import ru.tinkoff.kora.common.Component

@Component
class PlayerDamageService : Listener {
    var newAmplifier: Int? = null
    var hasAbsorption: Boolean? = null
    val damageToAbsorption = hashMapOf<String, Int>()

    operator fun get(name: String) = damageToAbsorption[name]

    operator fun get(player: Player) = get(player.name)

    fun invalidate(name: String) = damageToAbsorption.remove(name)

    fun increase(name: String, damage: Double) {
        val currentDmg = get(name)
        if (currentDmg != null) {
            damageToAbsorption[name] = (currentDmg + damage).toInt()
        } else {
            damageToAbsorption[name] = damage.toInt()
        }
    }


}