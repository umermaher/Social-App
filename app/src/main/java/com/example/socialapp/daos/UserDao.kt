package com.example.socialapp.daos

import com.example.socialapp.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    @DelicateCoroutinesApi
    fun addUser(user: User){
        user?.let {
            GlobalScope.launch (Dispatchers.IO){
                usersCollection.document(user.uid).set(it)
            }
        }
    }
    fun getUserById(uid:String): Task<DocumentSnapshot> {
        return usersCollection.document(uid).get()
    }
}