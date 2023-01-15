package com.llc.realtimechat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.llc.realtimechat.databinding.ActivityMainBinding
import com.llc.realtimechat.databinding.OneActivityBinding
import com.llc.realtimechat.register.RegisterViewEvent

class OneActivity: AppCompatActivity() {

    private lateinit var binding: OneActivityBinding

    private val viewModel:OneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OneActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnset.setOnClickListener {
            val userName = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()

            viewModel.upload(userName, phone)
        }

        viewModel.oneViewEventLiveData.observe(this) { event ->
            when (event) {
                is OneViewEvent.Success -> {
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }

                is OneViewEvent.Error -> {
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }

        }

    }
}