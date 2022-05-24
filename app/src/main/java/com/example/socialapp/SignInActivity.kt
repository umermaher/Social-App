package com.example.socialapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth:FirebaseAuth
    private companion object {
        private const val RC_SIGN_IN=99
        private const val TAG="SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //configure google sign in
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient=GoogleSignIn.getClient(this,gso)
        auth= Firebase.auth

        signInButton.setOnClickListener {
            signIn()
        }
    }
    private fun signIn(){
        val signInIntent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN->{
                val task=GoogleSignIn.getSignedInAccountFromIntent(data)
                handSignInTask(task)
            }
        }
    }

    private fun handSignInTask(completedTask: Task<GoogleSignInAccount>) {
        try{
            val account=completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG,"FirebaseAuthWithGoogle: ${account.id}")
            firebaseAuthWithGoogle(account.idToken)
        }catch (e:ApiException){
            Log.w(TAG,"Sign in: failed $e.statusCode")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        progressBar.visibility= View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            val auth= this@SignInActivity.auth.signInWithCredential(credential).await()
            val firebaseUser=auth.user
            withContext(Dispatchers.Main){
                updateUi(firebaseUser)
            }
        }

    }
    private fun updateUi(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null){
            progressBar.visibility= View.GONE
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            progressBar.visibility= View.GONE
        }
    }
}