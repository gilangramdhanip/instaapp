package com.project.instaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var reference : DatabaseReference
    lateinit var pd : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        textLogin.setOnClickListener {
            val goToLogin = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(goToLogin)
        }

        signup.setOnClickListener {
            pd = ProgressDialog(this@SignupActivity)
            pd.setMessage("Please Wait...")
            pd.show()

            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            val email = edtEmail.text.toString()
            val fullname = edtFullname.text.toString()

            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(fullname)){
                Toast.makeText(this@SignupActivity, "All Field are required", Toast.LENGTH_LONG).show()
            }else if(password.length < 6){
                Toast.makeText(this@SignupActivity, "Password must have 6 charaters", Toast.LENGTH_LONG).show()
            }else{
                register(username, password, email, fullname)
            }
        }



    }

    private fun register(username : String, password : String, email : String, fullname : String){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                var firebaseUser = auth.currentUser
                var userId = firebaseUser!!.uid

                reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                val hashMap : HashMap<String, Any> = HashMap()
                hashMap["id"] = userId
                hashMap["username"] = username.toLowerCase()
                hashMap["fullname"] = fullname
                hashMap["bio"] = ""
                hashMap["imageUrl"] = "https://firebasestorage.googleapis.com/v0/b/instaapp-83394.appspot.com/o/ic_profile.png?alt=media&token=96c38556-8625-4bf4-9742-2b9a612d04ad"

                reference.setValue(hashMap).addOnCompleteListener {
                    if(it.isSuccessful){
                        pd.dismiss()
                        val gotoLogin = Intent(this@SignupActivity, LoginActivity::class.java)
                        startActivity(gotoLogin)
                        finish()
                    }
                }
            }else{
                pd.dismiss()
                Toast.makeText(this@SignupActivity, "You can't register with this email and password", Toast.LENGTH_LONG).show()
            }
        }
    }
}