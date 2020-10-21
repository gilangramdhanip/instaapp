package com.project.instaapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.instaapp.MainActivity
import com.project.instaapp.R
import com.project.instaapp.model.Comment
import com.project.instaapp.model.Users
import org.w3c.dom.Text


class CommentAdapter(private var  mContext : Context, private var comments : List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private lateinit var firebaseUser: FirebaseUser


    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        var username : TextView = itemView.findViewById(R.id.username)
        var image_profile : ImageView = itemView.findViewById(R.id.image_profile)
        var comment : TextView = itemView.findViewById(R.id.commentItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_items, parent, false)
        return CommentAdapter.ViewHolder(view)
    }

    private fun userInfo(image_profile : ImageView, username : TextView, publisher : String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(publisher)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                Glide.with(mContext)
                    .load(user!!.imageUrl)
                    .into(image_profile)

                username.setText(user.username)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val comment = comments[position]

        holder.comment.setText(comment.comment)

        userInfo(holder.image_profile,holder.username, comment.publisher)

        holder.comment.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherid", comment.publisher)
            mContext.startActivity(intent)
        }

        holder.image_profile.setOnClickListener {
            val intent = Intent(mContext, MainActivity::class.java)
            intent.putExtra("publisherid", comment.publisher)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

}