package com.llc.realtimechat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {

    val chatListLiveData = MutableLiveData<List<Chat>>()

    private val chatNoteReference: DatabaseReference = Firebase.database.reference.child("chat")

    private val auth: FirebaseAuth = Firebase.auth

    init {
        //get data changes from firebase or read from database
        chatNoteReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                /* val chatList = snapshot.children.map {
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
                    val message = it.child("message").getValue<String>() ?: return@forEach
                    val sender = it.child("sender").getValue<String>() ?: return@forEach
                    val chatId = it.key ?: return@forEach
                    val chat = Chat(
                        chatId = chatId,
                        message = message,
                        userName = sender
                    )
                    chatList.add(chat)
                }
                chatListLiveData.postValue(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    fun sendMessage(message: String) {
        chatNoteReference.push().apply {
            child("sender").setValue(auth.currentUser?.email ?: "Dammy User")
            child("message").setValue(message)
        }
    }
}
