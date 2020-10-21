package com.project.instaapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.instaapp.R
import com.project.instaapp.adapter.UserAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import com.project.instaapp.model.Users as Users

class SearchFragment : Fragment() {

    private var mUsers = ArrayList<Users>()
    lateinit var userAdapter : UserAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var edtSearch : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchRv);
        edtSearch = view.findViewById(R.id.edtSearch)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userAdapter = UserAdapter(requireContext(), mUsers)
        recyclerView.adapter = userAdapter

        readUsers()
        edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUser(p0.toString().toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        // Inflate the layout for this fragment
        return view
    }

    private fun searchUser(string: String){
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(string)
            .endAt(string + "\uf0ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(Users::class.java)
                    mUsers.add(user!!)
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun readUsers(){
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(edtSearch.text.toString() == ""){
                    mUsers.clear()
                    for(snapshot in snapshot.children){
                        val user = snapshot.getValue(Users::class.java)
                        mUsers.add(user!!)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




}