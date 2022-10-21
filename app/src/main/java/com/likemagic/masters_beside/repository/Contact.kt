package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(var phone: String = "", var email:String = "", var vider: String = "", var telegram:String = ""):Parcelable
