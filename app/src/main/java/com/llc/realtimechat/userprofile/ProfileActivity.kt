package com.llc.realtimechat.userprofile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.llc.realtimechat.databinding.ProfileActivityBinding
import com.llc.realtimechat.extension.loadFromUrl
import com.llc.realtimechat.model.User

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ProfileActivityBinding

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.userProfile()
        viewModel.profileEvent.observe(this) { event ->
            when (event) {
                is ProfileViewEvent.Loading -> binding.progressBar.visibility = View.VISIBLE

                is ProfileViewEvent.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    bind(event.user)
                }

                is ProfileViewEvent.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun bind(user: User) {
        binding.imvProfile.loadFromUrl(user.profile)
        binding.txtEtName.setText(user.userName)
        binding.txtEtEmail.setText(user.email)
        binding.txtEtPassword.setText(user.password)
    }
}