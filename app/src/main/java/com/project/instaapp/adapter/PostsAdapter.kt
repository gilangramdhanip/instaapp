package com.project.instaapp.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.instaapp.CommentActivity
import com.project.instaapp.R
import com.project.instaapp.model.Posts
import com.project.instaapp.model.Users
import org.w3c.dom.Text


class PostsAdapter(private var  mContext : Context, private var posts : List<Posts>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private lateinit var firebaseUser: FirebaseUser


    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        var username :TextView = itemView.findViewById(R.id.username)
        var likes  :TextView= itemView.findViewById(R.id.likes)
        var publisher :TextView= itemView.findViewById(R.id.username)
        var description :TextView= itemView.findViewById(R.id.description)
        var comments : TextView = itemView.findViewById(R.id.comments)

        var image_profile : ImageView = itemView.findViewById(R.id.image_profile)
        var post_image : ImageView = itemView.findViewById(R.id.post_image)
        var like : ImageView = itemView.findViewById(R.id.like)
        var comment : ImageView = itemView.findViewById(R.id.comment)
        var save : ImageView = itemView.findViewById(R.id.save)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return PostsAdapter.ViewHolder(view)
    }

    private fun getComment(postId : String,comments : TextView){
        val reference = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                comments.setText("View All ${snapshot.childrenCount} Comments")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun publisherInfo(image_profile : ImageView, username : TextView, publisher : TextView, userId : String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                Glide.with(mContext)
                    .load(user!!.imageUrl)
                    .into(image_profile)

                username.setText(user.username)
                publisher.setText(user.username)


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun isLike(postId : String, imgView :ImageView){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Likes").child(postId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(firebaseUser!!.uid).exists()){
                    imgView.setImageResource(R.drawable.ic_liked)
                    imgView.setTag("liked")

                }else{
                    imgView.setImageResource(R.drawable.ic_like)
                    imgView.setTag("like")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun nrLikes(likes : TextView, postId: String){
        val reference = FirebaseDatabase.getInstance().getReference().child("Likes")
            .child(postId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                likes.setText("${snapshot.childrenCount} likes")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onBindViewHolder(holder: PostsAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val post = posts[position]

        Glide.with(mContext)
            .load(post.postimage)
            .into(holder.post_image)

        if(post.description == ""){
            holder.description.visibility = View.GONE
        }
        else{
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.description)
        }


        publisherInfo(holder.image_profile,holder.username, holder.publisher, post.publisher)
        isLike(post.postsid, holder.like)
        nrLikes(holder.likes, post.postsid)
        getComment(post.postsid, holder.comments)

        holder.like.setOnClickListener {
            if(holder.like.tag.equals("like")){
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postsid)
                    .child(firebaseUser.uid).setValue(true)
            }else{

                FirebaseDatabase.getInstance().reference.child("Likes").child(post.postsid)
                    .child(firebaseUser.uid).removeValue()
            }

        }

        holder.comment.setOnClickListener {

            val goToComment = Intent(mContext, CommentActivity::class.java)
            goToComment.putExtra("postsid", post.postsid)
            goToComment.putExtra("publisherid", post.publisher)
            mContext.startActivity(goToComment)

        }

        holder.comments.setOnClickListener {

            val goToComment = Intent(mContext, CommentActivity::class.java)
            goToComment.putExtra("postsid", post.postsid)
            goToComment.putExtra("publisherid", post.publisher)
            mContext.startActivity(goToComment)

        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

}