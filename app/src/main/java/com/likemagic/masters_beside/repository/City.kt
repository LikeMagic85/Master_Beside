package com.likemagic.masters_beside.repository

data class City(val name: String, val lat: Double, val lon: Double) {
    fun getCityList() = listOf(
        City("Минск", 53.9, 27.5667),
        City("Витебск", 55.1904, 30.2049),
        City("Брест", 52.0975, 23.6877),
        City("Гомель", 52.4345, 30.9754),
        City("Гродно", 53.6884, 23.8258),
        City("Могилев", 53.9168, 30.3449)
    )
}
