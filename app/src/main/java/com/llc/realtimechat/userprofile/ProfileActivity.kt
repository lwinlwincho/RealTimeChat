package com.llc.realtimechat.userprofile

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.databinding.ProfileActivityBinding
import com.llc.realtimechat.extension.loadFromUrl

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ProfileActivityBinding

    private val viewModel: ProfileViewModel by viewModels()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userEmail = FirebaseAuth.getInstance().currentUser!!.email

        db.collection("user").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    for (document in document) {

                        binding.imvProfile.loadFromUrl(document.data["profile"].toString())
                        binding.txtEtName.setText(document.data["userName"].toString())
                        binding.txtEtEmail.setText(document.data["email"].toString())
                        binding.txtEtPassword.setText(document.data["password"].toString())

                        Log.d("TAG", "${document.id}=>${document.data["email"]}")
                        Log.d("TAG", "$userEmail")
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        /*viewModel.oneViewEventLiveData.observe(this) { event ->
            when (event) {
                is ProfileViewEvent.Success -> {
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }

                is ProfileViewEvent.Error -> {
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }

        }*/

    }
}