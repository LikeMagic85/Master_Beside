package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profession(val name: String="", val keyWords: List<String> = listOf()):Parcelable
