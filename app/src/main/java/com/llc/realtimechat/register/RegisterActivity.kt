package com.llc.realtimechat.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.llc.realtimechat.MainActivity
import com.llc.realtimechat.databinding.ActivityRegisterBinding
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels()

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.registerViewEventLiveData.observe(this) { event ->
            when (event) {
                is RegisterViewEvent.Loading -> binding.progressBar.visibility = View.VISIBLE

                is RegisterViewEvent.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    //Login<Register<Main if you finish Register (Login<-<Main) when user click back btn it reach from Main to Login
                    //So finishAffinity() can help app entirely exit.(->->Main)
                    finishAffinity()
                }

                is RegisterViewEvent.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        binding.btnRegister.setOnClickListener {
            val userName = binding.txtEtName.text.toString()
            val email = binding.txtEtEmail.text.toString()
            val password = binding.txtEtPassword.text.toString()
            val passwordAgain = binding.txtEtPasswordAgain.text.toString()

            if (
                filePath != null
                && userName.isNotBlank()
                && email.isNotBlank()
                && password.isNotBlank()
                && password == passwordAgain
            ) {
                viewModel.registerWithEmail(filePath, userName, email, password)
            }
        }

        binding.imvProfile.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.imvProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

