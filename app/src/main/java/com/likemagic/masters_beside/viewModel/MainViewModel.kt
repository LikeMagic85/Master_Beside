package com.likemagic.masters_beside.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import com.likemagic.masters_beside.utils.*

class MainViewModel : ViewModel() {

    private val liveData: MutableLiveData<AppState> by lazy { MutableLiveData<AppState>() }


    fun getLiveData(): LiveData<AppState> {
        return liveData
    }

    private val accountBase = FirebaseAuth.getInstance()

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            liveData.postValue(AppState.Loading)
            accountBase.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    sendVerificationEmail(it.result?.user!!)
                } else if (it.isCanceled) {
                    liveData.postValue(AppState.ErrorSignIn(SIGN_ERROR))
                } else if (it.exception is FirebaseAuthUserCollisionException) {
                    val exception = it.exception as FirebaseAuthUserCollisionException
                    if (exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                        liveData.postValue(AppState.ErrorSignIn(ALREADY_REGISTER))
                        linkGoogleToEmail(email, password)
                    }
                }
            }
        }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                liveData.postValue(AppState.SuccessPostEmail(true, user.email!!))
            } else if (it.isCanceled) {
                liveData.postValue(AppState.SuccessPostEmail(false, "Гость"))
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            liveData.postValue((AppState.Loading))
            accountBase.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    liveData.postValue(
                        AppState.SuccessSignIn(
                            SUCCESSFUL_SIGN,
                            it.result.user?.email!!
                        )
                    )
                } else if (it.isCanceled) {
                    liveData.postValue(AppState.SuccessSignIn(SIGN_ERROR, "Гость"))
                } else if (it.exception is FirebaseAuthInvalidUserException) {
                    val exception = it.exception as FirebaseAuthInvalidUserException
                    if (exception.errorCode == USER_NOT_FOUND) {
                        liveData.postValue(AppState.ErrorSignIn(USER_NOT_FOUND))
                    }
                } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                    val exception = it.exception as FirebaseAuthInvalidCredentialsException
                    if (exception.errorCode == ERROR_WRONG_PASSWORD)
                        liveData.postValue(AppState.ErrorSignIn(ERROR_WRONG_PASSWORD))
                }
            }
        }
    }

    fun signOut() {
        accountBase.signOut()
        liveData.postValue(AppState.Logout)
    }

    fun resetPassword(email: String) {
        accountBase.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                liveData.postValue(AppState.SuccessReset)
            } else if (it.exception is FirebaseAuthInvalidUserException) {
                val exception = it.exception as FirebaseAuthInvalidUserException
                if (exception.errorCode == USER_NOT_FOUND) {
                    liveData.postValue(AppState.ErrorSignIn(USER_NOT_FOUND))
                }
            }
        }
    }

    fun signInWithGoogle(token:String, user: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(token, null)
        accountBase.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                liveData.postValue(AppState.SuccessSignInWithGoogle(user))
            }
        }
    }

    fun linkGoogleToEmail(email:String, password: String){
        val credential = EmailAuthProvider.getCredential(email, password)
        accountBase.currentUser?.linkWithCredential(credential)?.addOnCompleteListener {
            if(it.isSuccessful){
                liveData.postValue(AppState.SuccessLink(SUCCESS_LINK))
            }else{
                Log.d("@@@", it.exception.toString())
            }
        }
    }

    fun signInWithPhoneAuthCredential(verificationId:String, code:String){

    }

}