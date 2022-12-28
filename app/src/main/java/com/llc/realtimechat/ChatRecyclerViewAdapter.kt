package com.llc.realtimechat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.llc.realtimechat.databinding.ItemChatBinding

class ChatRecyclerViewAdapter() :
    ListAdapter<Chat, ChatRecyclerViewAdapter.ChatViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return ChatViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem: Chat = getItem(position)
        holder.bind(chatItem)
    }

    class ChatViewHolder(
        private var binding: ItemChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            with(binding) {
                tvName.text = chat.userName
                tvMessage.text = chat.message
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