package ru.remsoftware.utils.parser

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.tinkoff.kora.common.Component


@Component
class InventoryParser {
    private val BYPASS_CLASS = arrayOf(
        "CraftMetaBlockState", "CraftMetaItem", "GlowMetaItem"
    )

    fun inventoryToJson(inventory: Inventory): String {
        val gson = Gson()
        val inventoryJson = JsonArray()
        for (i in 0..40) {
            var itemStack: ItemStack? = inventory.getItem(i)
            if (itemStack == null) {
                itemStack = ItemStack(Material.AIR)
            }
            val jsonItem = JsonObject()
            val item = itemToJson(itemStack)
            inventoryJson.add(item)
        }
        return gson.toJson(inventoryJson)
    }

    fun jsonToInventory(json: String): Inventory {
        val parser = JsonParser()
        val inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER)
        val inventoryJson = parser.parse(json)
        if (inventoryJson is JsonArray) {
            for (i in 0..40) {
                val jsonItem = inventoryJson.get(i)
                val itemStack = jsonToItem(jsonItem.toString())
                inventory.setItem(i, itemStack)

            }
        }
        return inventory
    }

    fun itemToJson(itemStack: ItemStack): JsonObject {
        val itemJson = JsonObject()

        itemJson.addProperty("type", itemStack.type.name)
        if (itemStack.durability > 0) itemJson.addProperty("data", itemStack.durability)
        if (itemStack.amount != 1) itemJson.addProperty("amount", itemStack.amount)

        if (itemStack.hasItemMeta()) {
            val metaJson = JsonObject()
            val meta = itemStack.itemMeta

            if (meta.hasDisplayName()) {
                metaJson.addProperty("displayname", meta.displayName)
            }
            if (meta.hasLore()) {
                val lore = JsonArray()
                meta.lore.forEach { str -> lore.add(JsonPrimitive(str)) }
                metaJson.add("lore", lore)
            }
            if (meta.hasEnchants()) {
                val enchants = JsonArray()
                meta.enchants.forEach { (enchantment, integer) -> enchants.add(JsonPrimitive(enchantment.name + ":" + integer)) }
                metaJson.add("enchants", enchants)
            }
            if (!meta.itemFlags.isEmpty()) {
                val flags = JsonArray()
                meta.itemFlags.stream().map { obj: ItemFlag -> obj.name }.forEach { str -> flags.add(JsonPrimitive(str)) }
                metaJson.add("flags", flags)
            }

            for (clazz in BYPASS_CLASS) {
                if (meta.javaClass.getSimpleName().equals(clazz)) {
                    itemJson.add("item-meta", metaJson)
                    return itemJson
                }
            }
            if (meta is SkullMeta) {
                if (meta.hasOwner()) {
                    val extraMeta = JsonObject()
                    extraMeta.addProperty("owner", meta.owningPlayer.name)
                    metaJson.add("extra-meta", extraMeta)
                }
            } else if (meta is LeatherArmorMeta) {
                val extraMeta = JsonObject()
                extraMeta.addProperty("color", Integer.toHexString(meta.color.asRGB()))
                metaJson.add("extra-meta", extraMeta)
            } else if (meta is PotionMeta) {
                if (meta.hasCustomEffects()) {
                    val extraMeta = JsonObject()
                    val customEffects = JsonArray()
                    meta.customEffects.forEach { potionEffect ->
                        customEffects.add(
                            JsonPrimitive(
                                potionEffect.type.name
                                        + ":" + potionEffect.amplifier
                                        + ":" + potionEffect.duration / 20
                            )
                        )
                    }
                    extraMeta.add("custom-effects", customEffects)
                    metaJson.add("extra-meta", extraMeta)
                }
            }
            itemJson.add("item-meta", metaJson)
        }
        return itemJson
    }

    fun jsonToItem(string: String): ItemStack? {
        val parser = JsonParser()
        val itemJson = parser.parse(string)
        val itemObj = itemJson.asJsonObject
        val typeElement = itemObj.get("type")
        val dataElement = itemObj.get("data")
        val amountElement = itemObj.get("amount")
        val type = typeElement.asString
        var data: Short = 0; if (dataElement != null) data = dataElement.asShort
        var amount: Int = 1; if (amountElement != null) amount = amountElement.asInt

        val itemStack = ItemStack(Material.getMaterial(type))
        itemStack.durability = data
        itemStack.amount = amount

        val metaJson = itemObj.get("item-meta")
        if (metaJson != null) {
            val metaObj = metaJson.asJsonObject
            val meta = itemStack.itemMeta
            val displaynameElement = metaObj.get("displayname")
            val loreElement = metaObj.get("lore")
            val enchants = metaObj.get("enchants")
            val flagsElement = metaObj.get("flags")
            if (displaynameElement != null && displaynameElement is JsonPrimitive) meta.displayName = displaynameElement.asString
            if (loreElement != null && loreElement is JsonArray) {
                val lore = ArrayList<String>(loreElement.size())
                loreElement.forEach { jsonElement ->
                    lore.add(jsonElement.asString)
                }
                meta.lore = lore
            }
            if (enchants != null && enchants is JsonArray) {
                enchants.forEach { jsonElement ->
                    if (jsonElement is JsonPrimitive) {
                        val enchantString = jsonElement.asString
                        if (enchantString.contains(":")) {
                            val splitEnchant = enchantString.split(":")
                            val enchantment = Enchantment.getByName(splitEnchant[0])
                            val level = splitEnchant[1].toInt()
                            if (enchantment != null && level > 0) {
                                meta.addEnchant(enchantment, level, true)
                            }
                        }
                    }
                }
            }
            if (flagsElement != null && flagsElement is JsonArray) {
                flagsElement.forEach { jsonElement ->
                    if (jsonElement is JsonPrimitive) {
                        for (flag in ItemFlag.values()) {
                            if (flag.name.equals(jsonElement.asString, ignoreCase = true)) {
                                meta.addItemFlags(flag)
                                break
                            }
                        }
                    }
                }
            }
            for (clazz in BYPASS_CLASS) {
                if (meta.javaClass.simpleName.equals(clazz)) {
                    return itemStack
                }
            }
            val extraMetaElement = metaObj.get("extra-meta")
            if (extraMetaElement != null) {
                val extraJson = extraMetaElement.asJsonObject
                if (meta is SkullMeta) {
                    val ownerElement = extraJson.get("owner");
                    if (ownerElement != null && ownerElement is JsonPrimitive) {
                        val skullMeta = meta
                        val owner = Bukkit.getPlayer(ownerElement.asString)
                        skullMeta.setOwningPlayer(owner)
                    }
                } else if (meta is LeatherArmorMeta) {
                    val colorElement = extraJson.get("color")
                    if (colorElement != null && colorElement is JsonPrimitive) {
                        meta.color = Color.fromRGB(Integer.parseInt(colorElement.asString, 16) )
                    }
                } else if (meta is PotionMeta) {
                    val customEffectsElement = extraJson.get("custom-effects")
                    if (customEffectsElement != null && customEffectsElement is JsonArray) {
                        customEffectsElement.forEach { jsonElement ->
                            if (jsonElement is JsonPrimitive) {
                                val enchantString = jsonElement.asString
                                if (enchantString.contains(":")) {
                                    val splitEnchant = enchantString.split(":")
                                    val potionType = PotionEffectType.getByName(splitEnchant[0])
                                    val amplifier = splitEnchant[1].toInt()
                                    val duration = splitEnchant[2].toInt() * 20
                                    if (potionType != null) {
                                        meta.addCustomEffect(PotionEffect(potionType, amplifier, duration), true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            itemStack.itemMeta = meta
        }
        return itemStack
        return null
    }
}