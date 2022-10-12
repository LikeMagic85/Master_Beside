package com.likemagic.masters_beside.repository

data class Profession(val name: String, val keyWords:List<String>) {

    fun getAllProfession() = listOf(
        Profession("Строитель", listOf(
            "Построить дом",
            "Построить баню",
            "Построить гараж",
            "Построить сарай",
            "Накрыть крышу",
            "Положить кирпич",
            "Залить стяжку",
            "Оштукатурить стену",
            "Залить фундамент",
            "Положить блоки",
            "Положить кладку",
            "Сделать крышу"
        ))
    )

}
