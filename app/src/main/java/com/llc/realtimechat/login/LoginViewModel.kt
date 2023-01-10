package com.llc.realtimechat.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.R
import com.llc.realtimechat.SingleLiveEvent

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    val loginViewEventLiveData = SingleLiveEvent<LoginViewEvent>()


    fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginViewEventLiveData.postValue(LoginViewEvent.Success)
                } else {
                    Log.w("LoginActivity", "createUserWithEmail:failure", task.exception)
                    loginViewEventLiveData.postValue(LoginViewEvent.Error("Authentication failed."))
                }
            }
    }
}

sealed class LoginViewEvent {
    object Success : LoginViewEvent()
    data class Error(val message: String) : LoginViewEvent()
}
