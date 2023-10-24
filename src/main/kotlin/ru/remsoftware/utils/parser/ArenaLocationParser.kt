package ru.remsoftware.utils.parser;

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.bukkit.Location
import ru.tinkoff.kora.common.Component;

@Component
class ArenaLocationParser(
    private val locationParser: LocationParser,
) {

    fun jsonArrayToLocationList(jsonArray: String): ArrayList<Location> {
        val array = JsonParser().parse(jsonArray).asJsonArray
        val locationList = arrayListOf<Location>()
        for (element in array) {
            val location = locationParser.strToLoc(element.asString)
            locationList.add(location)
        }
        return locationList
    }

    fun locationListToJsonArray(list: ArrayList<Location>): String {
        val gson = Gson()
        val locationJson = JsonArray()
        for (location in list) {
            val stringLoc = locationParser.locToStr(location)
            locationJson.add(stringLoc)
        }
        return gson.toJson(locationJson)
    }
}
