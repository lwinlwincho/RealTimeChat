package com.llc.realtimechat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.model.Chat
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel : ViewModel(), FirebaseAuth.AuthStateListener {

    private val _chatListEvent = MutableLiveData<MainViewEvent>()
    val chatListEvent: LiveData<MainViewEvent> = _chatListEvent

    val isLoggedinLiveData = MutableLiveData<Boolean>()

    private val chatNoteReference: DatabaseReference = Firebase.database.reference.child("chat")

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    init {
        getProfile()
        getData()
    }

    private fun getProfile() {
        viewModelScope.launch {
            try {
                val userEmail = FirebaseAuth.getInstance().currentUser!!.email

                val documentRef =
                    db.collection("user").whereEqualTo("email", userEmail).get().await()

                if (documentRef != null) {
                    for (documentRef in documentRef) {
                        _chatListEvent.postValue(MainViewEvent.Profile(documentRef.data["profile"].toString()))
                    }
                }
            } catch (e: Exception) {
                _chatListEvent.postValue(MainViewEvent.Error(e.message.toString()))
            }
        }
    }

    private fun getData() {
        //get data changes from firebase or read from database
        chatNoteReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                /* val chatList = snapshot.children.map {
                 //if setvalue() doesn't complete,it return null
                     val message = it.child("message").getValue<String>() ?: ""
                     val sender = it.child("sender").getValue<String>() ?: ""
                     val chatId = it.key ?: ""
                     Chat(
                         chatId = chatId,
                         message = message,
                         userName = sender
                     )
                 }
                 chatRecyclerViewAdapter.submitList(chatList)
                 */

                val chatList = mutableListOf<Chat>()
                snapshot.children.forEach {

                    //if setvalue() doesn't complete,it doesn't return
                    val sender = it.child("sender").getValue<String>() ?: return@forEach
                    val message = it.child("message").getValue<String>() ?: return@forEach
                    val chatId = it.key ?: return@forEach

                    val chat = Chat(
                        chatId = chatId,
                        sender = sender,
                        message = message
                    )
                    chatList.add(chat)
                }
                _chatListEvent.postValue(MainViewEvent.Success(chatList))
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
        auth.addAuthStateListener(this)
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        val isLoggedIn = firebaseAuth.currentUser != null
        isLoggedinLiveData.postValue(isLoggedIn)
    }

    fun sendMessage(message: String) {
        chatNoteReference.push().apply {
            child("sender").setValue(auth.currentUser?.email ?: "Dammy User")
            child("message").setValue(message)
        }
    }

    fun logOut() {
        auth.signOut()
    }
}

sealed class MainViewEvent {
    object Loading : MainViewEvent()
    data class Success(val chatList: List<Chat>) : MainViewEvent()
    data class Profile(val profile: String) : MainViewEvent()
    data class Error(val message: String) : MainViewEvent()
}