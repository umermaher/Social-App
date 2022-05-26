package com.example.socialapp.ui

import android.app.DownloadManager
import android.content.Intent
import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.R
import com.example.socialapp.adapter.PostAdapter
import com.example.socialapp.daos.PostDao
import com.example.socialapp.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val rotateOpen:Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim) }
    private val rotateClose:Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim) }
    private val fromBottom:Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim) }
    private val toBottom:Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim) }

    private lateinit var postDao: PostDao
    private lateinit var postAdapter: PostAdapter
    private var clicked:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPostFab.setOnClickListener {
            onCreatePostFab()
        }
        writeFab.setOnClickListener {
//            Toast.makeText(this,"write",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,CreatePostActivity::class.java))
        }
        imageFab.setOnClickListener {
            Toast.makeText(this,"image",Toast.LENGTH_SHORT).show()
        }

        setUpRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        postAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        postAdapter.stopListening()
    }

    private fun setUpRecyclerView() {
        postDao= PostDao()
        val postCollection=postDao.postCollection
        val query=postCollection.orderBy("createdAt",Query.Direction.DESCENDING)
        val recyclerViewOption=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()

        postAdapter= PostAdapter(recyclerViewOption, getIPostAdapter())

        rvPost.layoutManager=LinearLayoutManager(this)
        rvPost.adapter=postAdapter
    }

    private fun getIPostAdapter(): PostAdapter.IPostAdapter = object : PostAdapter.IPostAdapter{
        override fun onLikedClicked(postId: String) {
            postDao.updateLikes(postId)
        }
    }

    private fun onCreatePostFab() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean){
        if(!clicked){
            createPostFab.startAnimation(rotateOpen)
            writeFab.startAnimation(fromBottom)
            imageFab.startAnimation(fromBottom)
        }else{
            createPostFab.startAnimation(rotateClose)
            writeFab.startAnimation(toBottom)
            imageFab.startAnimation(toBottom)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked){
            writeFab.visibility=View.VISIBLE
            imageFab.visibility=View.VISIBLE
        }else{
            writeFab.visibility=View.INVISIBLE
            imageFab.visibility=View.INVISIBLE
        }
    }
    private fun setClickable(clicked: Boolean) {
        if(!clicked){
            imageFab.isClickable=true
            writeFab.isClickable=true
        }else {
            imageFab.isClickable = false
            writeFab.isClickable = false
        }
    }
    fun signOut(view: android.view.View) {
        progressBarMain.visibility= View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            Firebase.auth.signOut()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"sign out!", Toast.LENGTH_LONG).show()
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}