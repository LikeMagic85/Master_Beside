package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(var name: String="", var lat: Double = 53.902284, var lon: Double=27.561831):Parcelable
