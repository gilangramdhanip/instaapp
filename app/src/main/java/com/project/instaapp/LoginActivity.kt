package com.project.instaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        textsignup.setOnClickListener {
            val goToSignUp = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(goToSignUp)
        }

        login.setOnClickListener {
            val pd = ProgressDialog(this@LoginActivity)
            pd.setMessage("Please Wait...")
            pd.show()

            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this@LoginActivity, "All fields are required", Toast.LENGTH_LONG).show()
            }else{

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(
                            auth.currentUser!!.uid)

                        reference.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                pd.dismiss()
                                val goToMain = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(goToMain)
                                finish()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                    else{
                        pd.dismiss()
                        Toast.makeText(this@LoginActivity, " Authentication failed!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}