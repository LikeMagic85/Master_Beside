package com.likemagic.masters_beside.viewModel


sealed class AppState{
    data class SuccessSignIn(var result: String, val name:String): AppState()
    data class ErrorSignIn(var result: String):AppState()
    data class SuccessPostEmail(var result: Boolean, val name:String): AppState()
    data class Error(val error: Throwable): AppState()
    object SuccessReset: AppState()
    object Logout: AppState()
    object Loading: AppState()
}