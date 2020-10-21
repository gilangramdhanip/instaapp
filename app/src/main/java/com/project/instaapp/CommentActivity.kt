package com.project.instaapp

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.project.instaapp.adapter.CommentAdapter
import com.project.instaapp.model.Comment
import com.project.instaapp.model.Users
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.post_item.*


class CommentActivity : AppCompatActivity() {


    private lateinit var firebaseUser : FirebaseUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentList : ArrayList<Comment>
    lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val toolbar = findViewById<Toolbar>(R.id.toolbarComment)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white));
        supportActionBar!!.title = "Comments"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.commentRecycleview)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(applicationContext, commentList)
        recyclerView.adapter = commentAdapter

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val postId = intent.getStringExtra("postsid")
        val publisherId = intent.getStringExtra("publisherid")

        post.setOnClickListener {
            if(add_comment.text.toString() == ""){
                Toast.makeText(
                    this@CommentActivity,
                    "You can't send empty comment",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                addComment(postId.toString())
            }
        }

        getImage()
        readComment(postId.toString())
    }

    private fun addComment(postId : String) {
        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId)

        val hashMap = HashMap<String, Any>()
        hashMap.put("comment", add_comment.text.toString())
        hashMap.put("publisher", firebaseUser.uid)

        reference.push().setValue(hashMap)
        add_comment.setText("")
    }

    private fun getImage(){
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                Glide.with(applicationContext)
                    .load(user!!.imageUrl)
                    .into(comment_profile)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


    private fun readComment(postId: String){
        val reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for( dataSnapshot in snapshot.children){
                    val comment = dataSnapshot.getValue(Comment::class.java)
                    commentList.add(comment!!)
                }

                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}