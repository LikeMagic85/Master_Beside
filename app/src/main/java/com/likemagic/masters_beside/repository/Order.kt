package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(val name: String, val id: Long): Parcelable
