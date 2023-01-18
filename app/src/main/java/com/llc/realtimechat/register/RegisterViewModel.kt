package com.llc.realtimechat.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.llc.realtimechat.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private var _registerViewEvent = SingleLiveEvent<RegisterViewEvent>()
    val registerViewEvent = _registerViewEvent

    private val auth: FirebaseAuth = Firebase.auth

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    private val db = FirebaseFirestore.getInstance()

    fun registerWithEmail(
        filePath: Uri?,
        userName: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _registerViewEvent.postValue(RegisterViewEvent.Loading)
            try {
                // Create Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()

                if (authResult.user != null) {

                    val imageRef = storageReference.child("images/" + UUID.randomUUID().toString())

                    // Upload Image
                    imageRef.putFile(filePath!!).await()

                    // DownLoad Image
                    val imageUri = imageRef.downloadUrl.await()

                    val userInfo = hashMapOf(
                        "profile" to imageUri.toString(),
                        "userName" to userName,
                        "email" to email,
                        "password" to password
                    )

                    db.collection("user").add(userInfo).await()

                    _registerViewEvent.postValue(RegisterViewEvent.Success)
                }
            } catch (e: Exception) {
                _registerViewEvent.postValue(RegisterViewEvent.Error(e.message.toString()))
            }
        }
    }

    fun register(
        filePath: Uri?,
        userName: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        uploadAndDownloadImage(
                            filePath = filePath,
                            success = { imageUrl ->
                                val userInfo = hashMapOf(
                                    "profile" to imageUrl,
                                    "userName" to userName,
                                    "email" to email,
                                    "password" to password
                                )
                                uploadUserInfo(userInfo)
                            }
                        )
                    }
                    .addOnFailureListener {
                        _registerViewEvent.postValue(RegisterViewEvent.Error(it.localizedMessage.orEmpty()))
                    }
            } catch (e: Exception) {
                _registerViewEvent.postValue(RegisterViewEvent.Error(e.message.toString()))
            }
        }
    }

    private fun uploadAndDownloadImage(filePath: Uri?, success: (String) -> Unit) {
        val ref = storageReference.child("images/" + UUID.randomUUID().toString())
        val uploadTask = ref.putFile(filePath!!)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { imageUri ->
                success.invoke(imageUri.toString())
            }
        }.addOnFailureListener {
            _registerViewEvent.postValue(RegisterViewEvent.Error(it.message.toString()))
        }
    }

    private fun uploadUserInfo(user: HashMap<String, String>) {
        db.collection("user")
            .add(user)
            .addOnSuccessListener {
                _registerViewEvent.postValue(
                    RegisterViewEvent.Success
                )
            }
            .addOnFailureListener {
                _registerViewEvent.postValue(
                    RegisterViewEvent.Error(it.localizedMessage.orEmpty())
                )
            }
    }
}

sealed class RegisterViewEvent {
    object Loading : RegisterViewEvent()
    object Success : RegisterViewEvent()
    data class Error(val message: String) : RegisterViewEvent()
}
