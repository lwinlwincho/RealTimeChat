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
import com.llc.realtimechat.detail.UpdateChatEvent
import com.llc.realtimechat.model.Chat
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    val registerViewEventLiveData = SingleLiveEvent<RegisterViewEvent>()

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
            registerViewEventLiveData.postValue(RegisterViewEvent.Loading)
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

                    val documentRef=db.collection("user").add(userInfo).await()

                    registerViewEventLiveData.postValue(RegisterViewEvent.Success(documentRef.id))
                }
            } catch (e: Exception) {
                registerViewEventLiveData.postValue(RegisterViewEvent.Error(e.message.toString()))
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
                    .addOnSuccessListener { task ->

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
                        registerViewEventLiveData.postValue(RegisterViewEvent.Error(it.localizedMessage.orEmpty()))
                    }
            } catch (e: Exception) {
                registerViewEventLiveData.postValue(RegisterViewEvent.Error(e.message.toString()))
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
            registerViewEventLiveData.postValue(RegisterViewEvent.Error(it.message.toString()))
        }
    }

    private fun uploadUserInfo(user: HashMap<String, String>) {
        db.collection("user")
            .add(user)
            .addOnSuccessListener {
                registerViewEventLiveData.postValue(
                    RegisterViewEvent.Success(it.id)
                )
            }
            .addOnFailureListener {
                registerViewEventLiveData.postValue(
                    RegisterViewEvent.Error(it.localizedMessage.orEmpty())
                )
            }
    }
}

sealed class RegisterViewEvent {
    object Loading : RegisterViewEvent()
    data class Success(val id : String) : RegisterViewEvent()
    data class Error(val message: String) : RegisterViewEvent()
}
