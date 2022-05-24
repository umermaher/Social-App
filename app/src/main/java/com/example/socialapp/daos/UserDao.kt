package com.example.socialapp.daos

import com.example.socialapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val dbCollection = db.collection("users")

    fun addUser(user: User){
        user?.let {
            GlobalScope.launch (Dispatchers.IO){
                dbCollection.document(user.uid).set(it)
            }
        }
    }
}