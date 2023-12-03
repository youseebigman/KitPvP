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
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import ru.tinkoff.kora.common.Component


@Component
class InventoryParser {
    private val BYPASS_CLASS = arrayOf(
        "CraftMetaBlockState", "CraftMetaItem", "GlowMetaItem"
    )

    fun inventoryToJson(inventory: Inventory): String {
        val gson = Gson()
        val inventoryJson = JsonArray()
        val inventoryObject = JsonObject()
        for (item in inventory) {
            if (item == null) {
                val itemJson: JsonObject? = null
                inventoryJson.add(itemJson)
            } else {
                val itemJson = itemToJson(item)
                inventoryJson.add(itemJson)
            }
        }
        inventoryObject.add("inventory", inventoryJson)
        inventoryObject.addProperty("inventory_type", inventory.type.name)
        return gson.toJson(inventoryObject)
    }

    fun jsonToInventory(json: String): Inventory {
        val parser = JsonParser()
        val inventoryJson = parser.parse(json)
        val inventoryObject = inventoryJson.asJsonObject
        val inventoryType = inventoryObject.get("inventory_type").asJsonPrimitive
        val inventory = Bukkit.getServer().createInventory(null, InventoryType.valueOf(inventoryType.asString))
        val inventoryArray = inventoryObject.get("inventory").asJsonArray
        for ((counter, item) in inventoryArray.withIndex()) {
            if (item.isJsonNull) {
                continue
            } else {
                val itemStack = jsonToItem(item.toString())
                inventory.setItem(counter, itemStack)
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
                val extraMeta = JsonObject()
                if (meta.hasCustomEffects()) {
                    val customEffects = JsonArray()
                    meta.customEffects.forEach { potionEffect ->
                        if (meta.color != null) {
                            customEffects.add(
                                JsonPrimitive(
                                    potionEffect.type.name
                                            + ":" + potionEffect.amplifier
                                            + ":" + potionEffect.duration / 20
                                            + ":" + Integer.toHexString(meta.color.asRGB())
                                )
                            )
                        } else {
                            customEffects.add(
                                JsonPrimitive(
                                    potionEffect.type.name
                                            + ":" + potionEffect.amplifier
                                            + ":" + potionEffect.duration / 20
                                )
                            )
                        }

                    }
                    extraMeta.add("custom-effects", customEffects)
                } else {
                    val type: PotionType = meta.basePotionData.type
                    val isExtended: Boolean = meta.basePotionData.isExtended
                    val isUpgraded: Boolean = meta.basePotionData.isUpgraded
                    val baseEffect = JsonObject()
                    baseEffect.addProperty("type", type.name)
                    baseEffect.addProperty("isExtended", isExtended)
                    baseEffect.addProperty("isUpgraded", isUpgraded)
                    extraMeta.add("base-effect", baseEffect)
                }
                metaJson.add("extra-meta", extraMeta)
            }
            itemJson.add("item-meta", metaJson)
        }
        return itemJson
    }

    fun jsonToItem(string: String): ItemStack {

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
                        meta.color = Color.fromRGB(Integer.parseInt(colorElement.asString, 16))
                    }
                } else if (meta is PotionMeta) {
                    val customEffectsElement = extraJson.get("custom-effects")
                    if (customEffectsElement != null && customEffectsElement is JsonArray) {
                        customEffectsElement.forEach { jsonElement ->
                            if (jsonElement is JsonPrimitive) {
                                val enchantString = jsonElement.asString
                                val splitEnchant = enchantString.split(":")
                                val potionType = PotionEffectType.getByName(splitEnchant[0])
                                val amplifier = splitEnchant[1].toInt()
                                val duration = splitEnchant[2].toInt() * 20
                                if (splitEnchant.size == 4) {
                                    meta.color = Color.fromRGB(Integer.parseInt(splitEnchant[3], 16))
                                }
                                meta.addCustomEffect(PotionEffect(potionType, duration, amplifier), true)
                            }
                        }
                    } else {
                        val basePotion = extraJson.getAsJsonObject("base-effect")
                        val potionType = PotionType.valueOf(basePotion["type"].asString)
                        val isExtended = basePotion["isExtended"].asBoolean
                        val isUpgraded = basePotion["isUpgraded"].asBoolean
                        val potionData = PotionData(potionType, isExtended, isUpgraded)
                        meta.basePotionData = potionData
                    }
                }
            }
            itemStack.itemMeta = meta
        }
        return itemStack
    }
}