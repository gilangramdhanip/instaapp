package com.project.instaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.project.instaapp.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var selectedFragment : Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener())

        val intent = intent.extras
        if(intent!=null){
            val publisher = intent.getString("publisherid")
            val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
            editor.putString("profileid", publisher)
            editor.apply()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()
            ).commit()
        }else{
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()
            ).commit()
        }

    }

    private fun navigationItemSelectedListener() =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.nav_search -> {
                    selectedFragment = SearchFragment()
                }
                R.id.nav_add -> {
                    selectedFragment = null
                    startActivity(Intent(this@MainActivity, AddActivity::class.java))
                }
                R.id.nav_love -> {
                    selectedFragment = LoveFragment()
                }
                R.id.nav_profile -> {
                    var editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                    editor.putString("profileid", FirebaseAuth.getInstance().currentUser!!.uid)
                    editor.apply()
                    selectedFragment = ProfileFragment()
                }
            }
            if(selectedFragment !=null){
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    selectedFragment!!
                ).commit()
            }

            true
        }
}