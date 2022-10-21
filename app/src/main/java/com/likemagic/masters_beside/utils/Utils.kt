package com.likemagic.masters_beside.utils

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.likemagic.masters_beside.R
import java.io.ByteArrayOutputStream

const val USER_CATEGORY = "user category"
const val GUEST = 1
const val USER = 2
const val MASTER = 3
const val MASTER_ID = "MASTER_ID"
const val DB_USER = "DB_USER"
const val DB_MASTER = "DB_MASTER"
const val ALREADY_REGISTER = "already register"
const val SUCCESSFUL_SIGN = "SUCCESSFUL_SIGN"
const val SIGN_ERROR = "SIGN_ERROR"
const val USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
const val PASSWORD = "PASSWORD"
const val SIGN_UP_WITH_PHONE_FRAGMENT = "SIGN_UP_WITH_PHONE_FRAGMENT"
const val SIGN_IN_WITH_EMAIL_FRAGMENT = "SIGN_IN_WITH_EMAIL_FRAGMENT"
const val SIGN_UP_WITH_EMAIL_FRAGMENT = "SIGN_UP_WITH_EMAIL_FRAGMENT"
const val CHOOSE_FRAGMENT = "CHOOSE_FRAGMENT"
const val SIGN_FRAGMENT = "SIGN_FRAGMENT"
const val CREATE_NEW_MASTER_FRAGMENT = "CREATE_NEW_MASTER_FRAGMENT"
const val CREATE_NEW_USER_FRAGMENT = "CREATE_NEW_USER_FRAGMENT"
const val LIST_OF_MASTERS_FRAGMENT = "LIST_OF_MASTERS_FRAGMENT"
const val RESET_PASSWORD_FRAGMENT =  "RESET_PASSWORD_FRAGMENT"
const val SIGN_IN_WITH_GOOGLE_REQUEST_CODE = 101
const val SUCCESS_LINK = "SUCCESS_LINK"
const val ERROR_INVALID_VERIFICATION_CODE = "ERROR_INVALID_VERIFICATION_CODE"
const val PROVIDER_PHONE = "phone"
const val PROVIDER_GOOGLE = "google.com"
const val PROVIDER_EMAIL = "password"
const val ABOUT_FRAGMENT = "ABOUT_FRAGMENT"
const val PROFILE_FRAGMENT = "PROFILE_FRAGMENT"
const val LINK_WITH_EMAIL_FRAGMENT =  "LINK_WITH_EMAIL_FRAGMENT"
const val ADD_IMAGE_REQUEST_CODE = 102
const val STORAGE_REQUEST_CODE = 103
const val IMAGES = "IMAGES"
const val CURRENT_MASTER = "CURRENT_MASTER"


open class Utils {

}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard(editText:androidx.appcompat.widget.AppCompatEditText) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
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

fun removeFragment(name:String, activity: FragmentActivity){
    activity.supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun setToolbarVisibility(activity: FragmentActivity, flag:Boolean){
    if(flag){
        activity.findViewById<ImageView>(R.id.toolbarImg).animate().scaleX(1f).scaleY(1f)
    }else{
        activity.findViewById<ImageView>(R.id.toolbarImg).animate().scaleX(0f).scaleY(0f)
    }
}

fun prepareImage(bitmap: Bitmap):ByteArray{
    val outStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outStream)
    return outStream.toByteArray()
}
