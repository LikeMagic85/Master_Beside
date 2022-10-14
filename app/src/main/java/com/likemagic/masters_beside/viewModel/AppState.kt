package com.likemagic.masters_beside.viewModel

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser


sealed class AppState{
    data class SuccessSignIn(var result: String, val name:String): AppState()
    data class SuccessLink(var result: String): AppState()
    data class ErrorSignIn(var result: String):AppState()
    data class SuccessPostEmail(var result: Boolean, val name:String): AppState()
    object SuccessReset: AppState()
    data class SuccessSignInWithGoogle(val user: GoogleSignInAccount, val isNew:Boolean): AppState()
    data class SuccessSignInWithPhone(val user: FirebaseUser, val isNew:Boolean): AppState()
    object ErrorVerificationCode: AppState()
    object Logout: AppState()
    object Loading: AppState()
}