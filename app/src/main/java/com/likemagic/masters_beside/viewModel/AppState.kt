package com.likemagic.masters_beside.viewModel

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.likemagic.masters_beside.repository.Master
import com.likemagic.masters_beside.repository.User


sealed class AppState{
    data class SuccessSignIn(var result: String, val name:String): AppState()
    data class SuccessLink(var result: String): AppState()
    data class ErrorSignIn(var result: String):AppState()
    data class SuccessPostEmail(var result: Boolean, val name:String): AppState()
    data class SuccessSignInWithGoogle(val user: GoogleSignInAccount, val isNew:Boolean): AppState()
    data class SuccessSignInWithPhone(val user: FirebaseUser, val isNew:Boolean,  val hasEmail:Boolean): AppState()
    data class NewMaster(val master: Master):AppState()
    data class ListOfMasters(val list:ArrayList<Master>):AppState()
    data class MasterPage(val master: Master, var isMy: Boolean, val showContacts: Boolean, val editState:Boolean, var isEmailChecked:Boolean, var isPhoneChecked: Boolean):AppState()
    data class UploadImage(val master: Master? = null, val user: User? =null):AppState()
    data class UpdateMaster(val master: Master,var  isNeed:Boolean):AppState()
    data class ConfirmPhone(val master: Master):AppState()
    data class ConfirmDone(val master: Master):AppState()
    data class EmptyList(var isEmpty:Boolean = false):AppState()
    data class NotRegUser(var isNotReg:Boolean = false):AppState()
    data class MyData(val master: Master):AppState()
    data class ListOfFavoriteMasters(val list:ArrayList<Master>):AppState()
    object PhoneInUse:AppState()
    object ErrorVerificationCode: AppState()
    object Logout: AppState()
    object Loading: AppState()
    object DeleteMaster:AppState()
    object SuccessReset: AppState()
}