package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Master(
    val profession: Profession = Profession(),
    val city: City = City(),
    var name: String = "",
    var about: String = "",
    var cost: String = "",
    val contact: Contact = Contact(),
    var rating: Double = 0.0,
    var vipStatus: Int = 0,
    var key:String? = null,
    var uid:String? = null,
    val reviews:List<Review> = listOf(),
    var age:String = "",
    var uriImage:String = "",
    var experience:String = "",
    val favorite:List<String> = listOf()
):Parcelable
