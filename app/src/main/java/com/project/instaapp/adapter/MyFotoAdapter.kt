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
import com.project.instaapp.model.Posts
import com.project.instaapp.model.Users

class MyFotoAdapter(private var  mContext : Context, private var foto : List<Posts>) : RecyclerView.Adapter<MyFotoAdapter.ViewHolder>() {

    private lateinit var firebaseUser: FirebaseUser


    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image_profile : ImageView = itemView.findViewById(R.id.post_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFotoAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item, parent, false)
        return MyFotoAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFotoAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val fotos = foto[position]

        Glide.with(mContext)
            .load(fotos.postimage)
            .into(holder.image_profile)
    }

    override fun getItemCount(): Int {
        return foto.size
    }

}