package com.example.socialapp.daos

import com.example.socialapp.model.Post
import com.example.socialapp.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    private val auth=Firebase.auth

    fun addPost(text:String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userDao=UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!

            val time=System.currentTimeMillis()
            val post= Post(text,user,time)
            postCollection.document().set(post)
        }
    }

    private fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    fun updateLikes(postId: String) {
        GlobalScope.launch (Dispatchers.IO){
            val currentUserId=auth.currentUser!!.uid
            val post=getPostById(postId).await().toObject(Post::class.java)!!
            val isLiked=post.likedBy.contains(currentUserId)

            if(isLiked)
                post.likedBy.remove(currentUserId)
            else
                post.likedBy.add(currentUserId)

            postCollection.document(postId).set(post)
        }
    }
}