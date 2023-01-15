package com.llc.realtimechat.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.llc.realtimechat.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class RegisterViewModel : ViewModel() {

    val registerViewEventLiveData = SingleLiveEvent<RegisterViewEvent>()

    private val auth: FirebaseAuth = Firebase.auth

    private lateinit var storageReference: StorageReference

    private val db = FirebaseFirestore.getInstance()
    //private val db = Firebase.firestore

    fun register(
        filePath: Uri,
        userName: String,
        email: String,
        password: String,
        passwordAgain: String
    ) {
        viewModelScope.launch {
            try {
                if (password != passwordAgain) {
                    registerViewEventLiveData.postValue(RegisterViewEvent.Error("Password does not match"))
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val user = hashMapOf(
                                "profile" to filePath,
                                "userName" to userName,
                                "email" to email,
                                "password" to password
                            )

                            if (filePath != null) {
                                val ref = storageReference?.child(
                                    "images/" + UUID.randomUUID().toString()
                                )
                                ref?.putFile(filePath!!)

                                db.collection("user").add(user).addOnCompleteListener {
                                    registerViewEventLiveData.postValue(
                                        RegisterViewEvent.Success
                                    )
                                }
                                    .addOnFailureListener {
                                        registerViewEventLiveData.postValue(
                                            RegisterViewEvent.Error("Failed Upload.")
                                        )
                                    }
                            } else {
                                registerViewEventLiveData.postValue(RegisterViewEvent.Error("Please Upload an Image."))
                            }
                        } else {
                            registerViewEventLiveData.postValue(RegisterViewEvent.Error("Authentication Failure."))
                        }
                    }
            } catch (e: Exception) {
                registerViewEventLiveData.postValue(RegisterViewEvent.Error(e.message.toString()))
            }
        }
    }
}

sealed class RegisterViewEvent {
    object Success : RegisterViewEvent()
    data class Error(val message: String) : RegisterViewEvent()
}
