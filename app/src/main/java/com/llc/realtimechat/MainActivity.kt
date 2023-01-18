package com.llc.realtimechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.llc.realtimechat.databinding.ActivityMainBinding
import com.llc.realtimechat.detail.DetailActivity
import com.llc.realtimechat.extension.loadFromUrl
import com.llc.realtimechat.login.LoginActivity
import com.llc.realtimechat.model.Chat
import com.llc.realtimechat.userprofile.ProfileActivity

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    private val viewModel: MainViewModel by viewModels()

    private val chatRecyclerViewAdapter: ChatRecyclerViewAdapter by lazy {
        ChatRecyclerViewAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isLoggedinLiveData.observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.chatListEvent.observe(this) { event ->
            when (event) {
                is MainViewEvent.Loading -> binding.progressBar.visibility = View.VISIBLE

                is MainViewEvent.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    chatRecyclerViewAdapter.submitList(event.chatList)
                }

                is MainViewEvent.Profile -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.imvProfile.loadFromUrl(event.profile)
                }

                is MainViewEvent.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        binding.rvChat.apply {
            adapter = chatRecyclerViewAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            viewModel.sendMessage(message)
        }

        binding.imvProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            viewModel.logOut()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCompleteTask(chat: Chat) {
        TODO("Not yet implemented")
    }

    override fun openDetails(chat: Chat) {
        val intent = Intent(this, DetailActivity::class.java)
        with(intent) {
            putExtra("chatId", chat.chatId)
            putExtra("sender", chat.sender)
            putExtra("message", chat.message)
            startActivity(this)
        }
    }


}