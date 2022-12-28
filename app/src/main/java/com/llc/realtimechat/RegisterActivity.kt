package com.llc.realtimechat

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.databinding.ActivityLoginBinding
import com.llc.realtimechat.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            val email = binding.txtEtEmail.text.toString()
            val password = binding.txtEtPassword.text.toString()
            val passwordAgain = binding.txtEtPasswordAgain.text.toString()

            if (password != passwordAgain) {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        //(Login>Register>Activity) if you finish() to RegisterActivity (Login>Activity) then back from Activity it reach Login .
                        //finishAffinity() can help either Register() or other Activity can finish .
                        //so if you back from Register(), app is entirely exit.
                        finishAffinity()
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

        }
    }
}
