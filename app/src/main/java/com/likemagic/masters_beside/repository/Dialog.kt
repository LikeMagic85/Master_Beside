package com.likemagic.masters_beside.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dialog(var members: String ="",
                  var senderName:String = "",
                  var recipientName:String = "",
                  var senderProfession: String = "",
                  var recipientProfession: String ="",
                  var senderUri:String = "",
                  var recipientUri: String = "",
                  var senderUid:String = "",
                  var recipientUid: String = "",
                  val listOfMessage:ArrayList<DialogMessage> = arrayListOf(),
                  var  key:String? =null):Parcelable
