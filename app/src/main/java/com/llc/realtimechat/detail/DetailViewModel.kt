package com.llc.realtimechat.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.model.Chat

class DetailViewModel : ViewModel() {

    val editChatLiveData = MutableLiveData<UpdateChatEvent>()

    private lateinit var dbRef: DatabaseReference

    fun updateMessage(chatId: String, sender: String, message: String) {

        dbRef = Firebase.database.reference.child("chat").child(chatId)
        val chat = Chat(chatId, sender, message)
        dbRef.setValue(chat)
        editChatLiveData.postValue(UpdateChatEvent.SuccessUpdate("Success updated!"))
    }

    fun deleteMessage(chatId: String) {
        dbRef = Firebase.database.reference.child("chat").child(chatId)
        dbRef.removeValue()
        editChatLiveData.postValue(UpdateChatEvent.SuccessUpdate("Success deleted!"))
    }
}

sealed class UpdateChatEvent {
    data class SuccessShow(val updateEvent: Chat) : UpdateChatEvent()
    data class SuccessUpdate(val message: String) : UpdateChatEvent()
    data class SuccessDelete(val message: String) : UpdateChatEvent()
    data class Error(val error: String) : UpdateChatEvent()

}