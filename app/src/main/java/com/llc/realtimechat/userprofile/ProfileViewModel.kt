package com.llc.realtimechat.userprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.SingleLiveEvent

class ProfileViewModel : ViewModel() {

    val oneViewEventLiveData = SingleLiveEvent<ProfileViewEvent>()

    private val db = Firebase.firestore

    fun userInfo() {

        val userId= FirebaseAuth.getInstance().currentUser!!.uid

        val docRef = db.collection("user").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

}

sealed class ProfileViewEvent {
    data class Success(val message: String) : ProfileViewEvent()
    data class Error(val message: String) : ProfileViewEvent()
}