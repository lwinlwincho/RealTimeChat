package com.llc.realtimechat

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OneViewModel: ViewModel() {

    val oneViewEventLiveData = SingleLiveEvent<OneViewEvent>()

    private val db = Firebase.firestore

    fun upload(userName:String,phone:String){

        val data = hashMapOf(
            "userName" to userName,
            "phone" to phone
        )

        //val userId= FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("user").add(data).addOnCompleteListener {
            oneViewEventLiveData.postValue(
                OneViewEvent.Success("Success Upload")
            )
        }
            .addOnFailureListener{
                oneViewEventLiveData.postValue(OneViewEvent.Error("Failed Upload."))
            }
    }

}

sealed class OneViewEvent {
    data class Success(val message:String) : OneViewEvent()
    data class Error(val message: String) : OneViewEvent()
}