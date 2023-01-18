package com.llc.realtimechat.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val _profileEvent = MutableLiveData<ProfileViewEvent>()
    val profileEvent: LiveData<ProfileViewEvent> = _profileEvent

    private val db = Firebase.firestore

    fun userProfile() {
        viewModelScope.launch {
            try {
                val userEmail = FirebaseAuth.getInstance().currentUser!!.email

                val documentRef =
                    db.collection("user").whereEqualTo("email", userEmail).get().await()

                if (documentRef != null) {
                    for (documentRef in documentRef) {
                        val user = User(
                            profile = documentRef.data["profile"].toString(),
                            userName = documentRef.data["userName"].toString(),
                            email = documentRef.data["email"].toString(),
                            password = documentRef.data["password"].toString()
                        )
                        _profileEvent.postValue(ProfileViewEvent.Success(user))
                    }
                }
            } catch (e: Exception) {
                _profileEvent.postValue(ProfileViewEvent.Error(e.message.toString()))
            }
        }
    }
}

sealed class ProfileViewEvent {
    object Loading:ProfileViewEvent()
    data class Success(val user: User) : ProfileViewEvent()
    data class Error(val message: String) : ProfileViewEvent()
}