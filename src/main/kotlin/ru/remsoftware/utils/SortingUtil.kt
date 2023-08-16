package ru.remsoftware.utils

object SortingUtil {

    fun sortListWithStartLetters(list: List<String>, letters: String): MutableList<String> {
        val returnedList: MutableList<String> = mutableListOf()
        for (word in list) {
            if (word.startsWith(letters)) {
                returnedList.add(word)
            }
        }
        return returnedList
    }
    fun sortListWithContainsLetters(list: List<String>, letters: String): MutableList<String> {
        val returnedList: MutableList<String> = mutableListOf()

        for (word in list) {
            if (word.contains(letters.uppercase())) {
                returnedList.add(word)
            }
        }
        return returnedList
    }
}