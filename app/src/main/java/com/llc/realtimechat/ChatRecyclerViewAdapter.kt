package com.llc.realtimechat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.llc.realtimechat.databinding.ItemChatBinding
import com.llc.realtimechat.model.Chat

interface OnItemClickListener {
    fun onCompleteTask(chat: Chat)
    fun openDetails(chat: Chat)
}

class ChatRecyclerViewAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<Chat, ChatRecyclerViewAdapter.ChatViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return ChatViewHolder(
            ItemChatBinding.inflate(LayoutInflater.from(parent.context)),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem: Chat = getItem(position)
        holder.bind(chatItem)
    }

    class ChatViewHolder(
        private var binding: ItemChatBinding,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            with(binding) {
                tvSender.text = chat.sender
                tvMessage.text = chat.message

                imvEdit.setOnClickListener {
                    onItemClickListener.openDetails(chat)
                }
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Chat>() {

        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
}