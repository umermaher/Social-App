package com.example.socialapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialapp.R
import com.example.socialapp.daos.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postDao:PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postDao=PostDao()
        postButton.setOnClickListener {
            val input=postEditText.text.toString().trim()
            if(input.isNotEmpty()){
                postDao.addPost(input)
                finish()
            }
        }
    }
}