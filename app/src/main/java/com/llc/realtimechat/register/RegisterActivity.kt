package com.llc.realtimechat.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.llc.realtimechat.MainActivity
import com.llc.realtimechat.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.registerViewEventLiveData.observe(this) { event ->
            when (event) {
                is RegisterViewEvent.Success -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    //Login<Register<Main if you finish Register (Login<-<Main) when user click back btn it reach from Main to Login
                    //So finishAffinity() can help app entirely exit.(->->Main)
                    finishAffinity()
                }

                is RegisterViewEvent.Error -> {
                    Toast.makeText(baseContext, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }

        }

        binding.btnRegister.setOnClickListener {
            val email = binding.txtEtEmail.text.toString()
            val password = binding.txtEtPassword.text.toString()
            val passwordAgain = binding.txtEtPasswordAgain.text.toString()

            viewModel.register(email,password,passwordAgain)
        }
    }
}
