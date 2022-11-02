package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(val owner: Master, val rating: Int, val text:String):Parcelable