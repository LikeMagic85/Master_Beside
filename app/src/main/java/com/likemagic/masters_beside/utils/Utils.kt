package com.likemagic.masters_beside.utils

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.likemagic.masters_beside.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


const val MASTER_ID = "MASTER_ID"
const val DB_MASTER = "DB_MASTER"
const val PHONE = "PHONE"
const val ALREADY_REGISTER = "already register"
const val SUCCESSFUL_SIGN = "SUCCESSFUL_SIGN"
const val SIGN_ERROR = "SIGN_ERROR"
const val USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
const val PASSWORD = "PASSWORD"
const val SIGN_UP_WITH_PHONE_FRAGMENT = "SIGN_UP_WITH_PHONE_FRAGMENT"
const val SIGN_IN_WITH_EMAIL_FRAGMENT = "SIGN_IN_WITH_EMAIL_FRAGMENT"
const val SIGN_UP_WITH_EMAIL_FRAGMENT = "SIGN_UP_WITH_EMAIL_FRAGMENT"
const val FAVORITE_LIST_FRAGMENT = "FAVORITE_LIST_FRAGMENT"
const val SIGN_FRAGMENT = "SIGN_FRAGMENT"
const val CREATE_NEW_MASTER_FRAGMENT = "CREATE_NEW_MASTER_FRAGMENT"
const val LIST_OF_MASTERS_FRAGMENT = "LIST_OF_MASTERS_FRAGMENT"
const val RESET_PASSWORD_FRAGMENT =  "RESET_PASSWORD_FRAGMENT"
const val LINK_PHONE_FRAGMENT =  "LINK_PHONE_FRAGMENT"
const val SUCCESS_LINK = "SUCCESS_LINK"
const val ERROR_INVALID_VERIFICATION_CODE = "ERROR_INVALID_VERIFICATION_CODE"
const val ERROR_CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE"
const val PROVIDER_PHONE = "phone"
const val PROVIDER_GOOGLE = "google.com"
const val PROVIDER_EMAIL = "password"
const val ABOUT_FRAGMENT = "ABOUT_FRAGMENT"
const val PROFILE_FRAGMENT = "PROFILE_FRAGMENT"
const val LINK_WITH_EMAIL_FRAGMENT =  "LINK_WITH_EMAIL_FRAGMENT"
const val LIST_OF_REVIEWS_FRAGMENT =  "LIST_OF_REVIEWS_FRAGMENT"
const val STORAGE_REQUEST_CODE = 103
const val IMAGES = "IMAGES"
const val REGISTER_EMAIL = "EMAIL"
const val TIME_DELAY = 2000
const val NOT_MASTER = "NOT_MASTER"
const val JOBS_BRANCH = "JOBS_BRANCH"
const val JOBS_LIST_FRAGMENT =  "JOBS_LIST_FRAGMENT"
const val JOB_FRAGMENT =  "JOB_FRAGMENT"
const val JOB_ID = "JOB_ID"
const val MASTER_REV = "MASTER_REV"
const val DIALOGS_BRANCH = "DIALOGS_BRANCH"
const val DIALOG_LIST_FRAGMENT =  "DIALOG_LIST_FRAGMENT"
const val DIALOG_ID = "DIALOG_ID"
const val DIALOG_FRAGMENT =  "DIALOG_FRAGMENT"
const val SERVER_KEY_FCM = "XXX"


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun isValidEmail(target: CharSequence?): Boolean {
    return if (TextUtils.isEmpty(target)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }
}

fun isValidPassword(password: String?) : Boolean {
    password?.let {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return passwordMatcher.find(password) != null
    } ?: return false
}

fun isValidPhone(phone:String?):Boolean{
    phone?.let{
        val phonePattern = "^[+]?[0-9]{3}?[-\\s.]?[0-9]{2}[-\\s.]?[0-9]{7}\$"
        val phoneMatcher = Regex(phonePattern)
        return phoneMatcher.find(phone) != null
    } ?: return false
}

fun removeFragment(name:String, activity: FragmentActivity){
    activity.supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun delFragment(fragment: Fragment, activity: FragmentActivity){
    activity.supportFragmentManager.beginTransaction()
        .remove(fragment).commit()
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

fun navigateTo(fragment: Fragment, name: String, activity: FragmentActivity) {
    activity.supportFragmentManager
        .beginTransaction()
        .addToBackStack(name)
        .replace(R.id.mainContainer, fragment, name)
        .commit()
}

fun navigateToAndAdd(fragment: Fragment, name: String, activity: FragmentActivity) {
    activity.supportFragmentManager
        .beginTransaction()
        .add(R.id.mainContainer, fragment, name)
        .addToBackStack(name)
        .commit()
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("dd.MM HH:mm", Locale("ru"))
    return format.format(date)
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}


