package com.vchat.muhammadfaizan.vchat.views

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.vchat.muhammadfaizan.vchat.R

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var mNav: BottomNavigationView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressbar: ProgressBar
    lateinit var home : HomeFragment
    lateinit var friends : FriendsFragment
    lateinit var profile : ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setFragment(HomeFragment())
        setListener()
    }

    private fun initViews() {
        mNav = findViewById(R.id.mNav)
        firebaseAuth = FirebaseAuth.getInstance()
        progressbar = findViewById(R.id.mainPBar)
        home = HomeFragment()
        friends = FriendsFragment()
        profile = ProfileFragment()
    }

    private fun setListener() {
        mNav.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                setFragment(home)
                return true
            }
            R.id.friends -> {
                setFragment(friends)
                return true
            }
            R.id.profile -> {
                setFragment(profile)
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        var setFragObj: FragmentTransaction = supportFragmentManager.beginTransaction()
        setFragObj.replace(R.id.mFrame, fragment)
        setFragObj.commit()
    }
}
