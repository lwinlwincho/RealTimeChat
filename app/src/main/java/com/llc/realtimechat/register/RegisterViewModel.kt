package com.llc.realtimechat.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.SingleLiveEvent

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    val registerViewEventLiveData = SingleLiveEvent<RegisterViewEvent>()

    fun register(email: String, password: String, passwordAgain: String) {

        if (password != passwordAgain) {
            registerViewEventLiveData.postValue(RegisterViewEvent.Error("Password does not match"))
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    registerViewEventLiveData.postValue(RegisterViewEvent.Success)
                }
                else {
                    registerViewEventLiveData.postValue(RegisterViewEvent.Error("Authentication Failure."))
                }
            }
    }
}

sealed class RegisterViewEvent {
    object Success : RegisterViewEvent()
    data class Error(val message: String) : RegisterViewEvent()
}
