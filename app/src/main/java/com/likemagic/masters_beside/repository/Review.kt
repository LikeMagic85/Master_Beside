package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(val ownerName: String = "", val ownerPhoto:String = "", val rating: Int = 1, val text:String = ""):Parcelable