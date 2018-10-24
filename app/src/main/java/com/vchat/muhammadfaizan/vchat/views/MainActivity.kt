package com.vchat.muhammadfaizan.vchat.views

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vchat.muhammadfaizan.vchat.R

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var mNav: BottomNavigationView
    lateinit var home: HomeFragment
    lateinit var user: UserFragmnet
    lateinit var notification: NotificationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setFragment(HomeFragment())
        setListener()
    }

    fun initViews() {
        mNav = findViewById(R.id.mNav)
        home = HomeFragment()
        user = UserFragmnet()
        notification = NotificationFragment()
    }

    fun setListener() {
        mNav.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                setFragment(home)
                return true
            }
            R.id.user -> {
                setFragment(user)
                return true
            }
            R.id.notification -> {
                setFragment(notification)
                return true
            }
            else -> {
                return false
            }
        }
    }

    fun setFragment(fragment: Fragment) {
        var setFragObj: FragmentTransaction = supportFragmentManager.beginTransaction()
        setFragObj.replace(R.id.mFrame, fragment)
        setFragObj.commit()
    }
}
