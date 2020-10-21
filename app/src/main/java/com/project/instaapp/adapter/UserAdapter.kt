package com.project.instaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.project.instaapp.fragment.ProfileFragment
import com.project.instaapp.model.Users
import com.project.instaapp.R


class UserAdapter(private var  mContext : Context, private var users : List<Users>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var firebaseUser: FirebaseUser

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
         var username : TextView = itemView.findViewById(R.id.username)
         var fullname : TextView = itemView.findViewById(R.id.fullname)
         var imageView : ImageView = itemView.findViewById(R.id.imageView_profile)
         var btn_follow : Button = itemView.findViewById(R.id.btn_follow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
       firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val user = users[position]

        holder.btn_follow.visibility = View.VISIBLE
        holder.username.text = user.username
        holder.fullname.text = user.fullname

        Glide.with(mContext)
            .load(user.imageUrl)
            .into(holder.imageView)

        isFollowing(user.id, holder.btn_follow)

        if(user.id.equals(firebaseUser.uid)){
            holder.btn_follow.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("profileid", user.id)
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.btn_follow.setOnClickListener {
            if(holder.btn_follow.text.toString().equals("follow")){
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid).child("following")
                    .child(user.id).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id).child("followers")
                    .child(firebaseUser.uid).setValue(true)
            }else{

                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid).child("following")
                    .child(user.id).removeValue()
                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id).child("followers")
                    .child(firebaseUser.uid).removeValue()
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    private fun isFollowing(userId: String, button: Button){
        val reference = FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid).child(
            "following"
        )

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(userId).exists()) {
                    button.setText("following")
                } else {
                    button.setText("follow")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
