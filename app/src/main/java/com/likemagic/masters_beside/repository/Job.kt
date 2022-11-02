package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(var ownerName:String = "",
               var name:String = "",
               var message:String = "",
               var city: City = City(),
               val date:Long = 0,
               var uid:String? = null,
               var id:String? = null,
               var uriImage:String = "",
               var key:String? = null,
               var cost: String = "",
               var vipStatus: Boolean = false):Parcelable
