package com.likemagic.masters_beside.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager

const val USER_CATEGORY = "user category"
const val GUEST = 1
const val USER = 2
const val MASTER = 3
const val ALREADY_REGISTER = "already register"
const val SUCCESSFUL_SIGN = "SUCCESSFUL_SIGN"
const val SIGN_ERROR = "SIGN_ERROR"
const val USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
const val PASSWORD = "PASSWORD"
const val SIGN_IN_WITH_EMAIL_FRAGMENT = "SIGN_IN_WITH_EMAIL_FRAGMENT"
const val SIGN_UP_WITH_EMAIL_FRAGMENT = "SIGN_UP_WITH_EMAIL_FRAGMENT"
const val SIGN_UP_FRAGMENT = "SIGN_UP_FRAGMENT"
const val SIGN_FRAGMENT = "SIGN_FRAGMENT"
const val CREATE_NEW_MASTER_FRAGMENT = "CREATE_NEW_MASTER_FRAGMENT"
const val LIST_OF_MASTERS_FRAGMENT = "LIST_OF_MASTERS_FRAGMENT"
const val RESET_PASSWORD_FRAGMENT =  "RESET_PASSWORD_FRAGMENT"

class Utils {
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun isValidEmail(target: CharSequence?): Boolean {
    return if (TextUtils.isEmpty(target)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

fun isValidPassword(password: String?) : Boolean {
    password?.let {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return passwordMatcher.find(password) != null
    } ?: return false
}