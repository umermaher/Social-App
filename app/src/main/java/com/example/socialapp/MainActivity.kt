package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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