package com.project.instaapp.fragment

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.instaapp.R
import com.project.instaapp.adapter.MyFotoAdapter
import com.project.instaapp.model.Posts
import com.project.instaapp.model.Users
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId : String
    private lateinit var fotolist : ArrayList<Posts>
    private lateinit var edit_profile : Button
    private lateinit var following : TextView
    private lateinit var follower : TextView
    private lateinit var recycleView: RecyclerView
    private lateinit var myFotoAdapter: MyFotoAdapter
    private lateinit var myTag : ImageButton
    private lateinit var posts : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val preference = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileId = preference.getString("profileid","none")!!
        // Inflate the layout for this fragment

        edit_profile = view.findViewById(R.id.edit_profile)
        follower = view.findViewById(R.id.follower)
        following = view.findViewById(R.id.following)
        recycleView = view.findViewById(R.id.recycleView)
        myTag = view.findViewById(R.id.myTag)
        posts = view.findViewById(R.id.posts)

        recycleView.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager(requireContext(), 3)
        recycleView.layoutManager = linearLayoutManager
        fotolist = ArrayList()
        myFotoAdapter = MyFotoAdapter(requireContext(), fotolist)
        recycleView.adapter = myFotoAdapter

        userInfo()
        getFollow()
        getNrPosts()
        myFoto()

        if(profileId == firebaseUser.uid){
            edit_profile.setText("Edit Profile")
        }else{
            checkFollow()
            myTag.visibility = View.GONE
        }

        edit_profile.setOnClickListener {
            val btn = edit_profile.text.toString()

            if(btn == "Edit Profile"){
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid).child("following")
                    .child(profileId).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileId).child("followers")
                    .child(firebaseUser.uid).setValue(true)
            }else if(btn == "following"){
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid).child("following")
                    .child(profileId).removeValue()
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileId).child("followers")
                    .child(firebaseUser.uid).removeValue()
            }

        }
        return view
    }


    private fun userInfo(){
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(context == null){
                    return
                }

                val user = snapshot.getValue(Users::class.java)
                Glide.with(requireActivity())
                    .load(user!!.imageUrl)
                    .into(image_profile)
                username.text = user.username
                fullname.text = user.fullname
                bio.text = user.bio

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkFollow(){
        val reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.uid).child("following")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(profileId).exists()){
                    edit_profile.setText("following")
                }else{
                    edit_profile.setText("follow")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollow(){
        val reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                follower.setText("${snapshot.childrenCount}")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following")
        reference1.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                following.setText("${snapshot.childrenCount}")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getNrPosts(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for(datasnapshot in snapshot.children){
                    val post = datasnapshot.getValue(Posts::class.java)
                    if(post!!.publisher == profileId){
                        i += 1
                    }
                }

                posts.setText("$i")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun myFoto(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                fotolist.clear()

                for(datasnapshot in snapshot.children){
                    val post = datasnapshot.getValue(Posts::class.java)
                    if(post!!.publisher == profileId){
                        fotolist.add(post)
                    }
                }

                Collections.reverse(fotolist)
                myFotoAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}