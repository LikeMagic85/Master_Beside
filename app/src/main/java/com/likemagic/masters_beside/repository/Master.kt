package com.likemagic.masters_beside.repository

data class Master(
    val profession: Profession,
    var name: String,
    var about: String,
    val city: City,
    var cost: String,
    val contact: Contact,
    var rating: Float,
    var vipStatus: Int
) {

    fun getAllMasters() = listOf<Master>()

    fun makeJob(order: Order) {

    }
}
