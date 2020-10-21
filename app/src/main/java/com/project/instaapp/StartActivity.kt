package com.project.instaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    lateinit var firebaseAuth : FirebaseAuth

    override fun onStart() {
        super.onStart()

        var firebaseUser = firebaseAuth.currentUser

        if(firebaseUser!=null){
            startActivity( Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        firebaseAuth = FirebaseAuth.getInstance()


        login.setOnClickListener {
            val goToLogin = Intent(this@StartActivity, LoginActivity::class.java)
            startActivity(goToLogin)
        }

        signup.setOnClickListener {
            val goToSignUp = Intent(this@StartActivity, SignupActivity::class.java)
            startActivity(goToSignUp)
        }

    }
}