package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val login: String,
    val order: Order
): Parcelable
