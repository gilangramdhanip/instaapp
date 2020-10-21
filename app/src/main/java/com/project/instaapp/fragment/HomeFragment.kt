package com.project.instaapp.fragment

import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.instaapp.R
import com.project.instaapp.adapter.PostsAdapter
import com.project.instaapp.model.Posts

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var postList : ArrayList<Posts>
    private lateinit var followingList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.homeRecycleView)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        postList = ArrayList()
        recyclerView.layoutManager = linearLayoutManager
        postsAdapter = PostsAdapter(requireContext(), postList)
        recyclerView.adapter =postsAdapter

        checkFollowing()
        return view
    }

    private fun checkFollowing(){
        followingList = ArrayList()

        val reference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("following")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                followingList.clear()
                for(dataSnapshot in snapshot.children){
                    followingList.add(dataSnapshot.key.toString())
                }
                readPost()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun readPost(){
        val reference = FirebaseDatabase.getInstance().getReference("Posts")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for( datasnapshot in snapshot.children){
                    val post = datasnapshot.getValue(Posts::class.java)
                    for(id in followingList){
                        if(post!!.publisher == id){
                            postList.add(post)
                        }

                        if(post!!.publisher == FirebaseAuth.getInstance().currentUser!!.uid){
                            postList.add(post)
                        }
                    }
                }
                postsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}