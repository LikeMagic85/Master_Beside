package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Master(
    var profession: Profession = Profession(),
    var city: City = City(),
    var name: String = "",
    var about: String = "",
    var cost: String = "",
    var contact: Contact = Contact(),
    var rating: Double = 0.0,
    var vipStatus: Boolean = false,
    var key:String? = null,
    var uid:String? = null,
    val reviews:ArrayList<Review> = arrayListOf(),
    var age:String = "",
    var uriImage:String = "",
    var experience:String = "",
    val favorite:ArrayList<String> = arrayListOf(),
    var isPhoneChecked: Boolean = false,
    var isEmailChecked:Boolean = false,
    var statusOnline:String = "",
    var lastVisit:Long = 0,
    var fcmToken:String = ""
):Parcelable
