package com.llc.realtimechat.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.llc.realtimechat.MainActivity
import com.llc.realtimechat.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtEtSender.setText(intent.getStringExtra("sender"))
        binding.txtEtMessage.setText(intent.getStringExtra("message"))

        binding.btnUpdate.setOnClickListener {

            val chatId = intent.getStringExtra("chatId")
            val sender = binding.txtEtSender.text.toString()
            val message = binding.txtEtMessage.text.toString()

            if (chatId != null) {
                viewModel.updateMessage(chatId, sender, message)
            }
        }

        binding.btnDelete.setOnClickListener {

            val chatId = intent.getStringExtra("chatId")
            if (chatId != null) {
                viewModel.deleteMessage(chatId)
            }
        }

        viewModel.editChatLiveData.observe(this) {
            when (it) {
                is UpdateChatEvent.SuccessUpdate -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                is UpdateChatEvent.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                is UpdateChatEvent.SuccessDelete -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

    }
}