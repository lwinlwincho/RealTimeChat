package com.llc.realtimechat.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.SingleLiveEvent

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private var _loginViewEvent = SingleLiveEvent<LoginViewEvent>()
    val loginViewEvent = _loginViewEvent

    fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginViewEvent.postValue(LoginViewEvent.Success)
                } else {
                    _loginViewEvent.postValue(LoginViewEvent.Error(task.exception.toString()))
                }
            }
    }
}

sealed class LoginViewEvent {
    object Success : LoginViewEvent()
    data class Error(val message: String) : LoginViewEvent()
}
