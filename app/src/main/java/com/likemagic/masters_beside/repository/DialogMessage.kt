package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogMessage(val owner:String ="", val message: String="", val time:Long = 0):Parcelable
