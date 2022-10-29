package com.likemagic.masters_beside.repository

import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.likemagic.masters_beside.R
import com.likemagic.masters_beside.utils.SIGN_UP_WITH_PHONE_FRAGMENT
import com.likemagic.masters_beside.utils.removeFragment
import java.util.concurrent.TimeUnit

class SignInWithPhone(private val accountBase: FirebaseAuth, var id:String ="") {

    fun requestConfirmCode(phone: String, activity: FragmentActivity) {
        val options = PhoneAuthOptions.newBuilder(accountBase)
            .setPhoneNumber(phone)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                }
                override fun onVerificationFailed(e: FirebaseException) {
                    val view = activity.findViewById<FrameLayout>(R.id.mainContainer)
                    Log.d("@@@", "onVerificationFailed", e)
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(view, "Пожалуйста, введите существующий номер телефона", Snackbar.LENGTH_SHORT).show()
                        removeFragment(SIGN_UP_WITH_PHONE_FRAGMENT, activity)
                    } else if (e is FirebaseTooManyRequestsException) {
                        Snackbar.make(view, "Вы сделали слишком много запросов", Snackbar.LENGTH_SHORT).show()
                    }
                }
                override fun onCodeSent(idToken: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(idToken ,p1)
                    id = idToken
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


}