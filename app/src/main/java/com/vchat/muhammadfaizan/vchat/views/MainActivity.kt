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
    private lateinit var home: HomeFragment
    private lateinit var user: UserFragmnet
    private lateinit var notification: NotificationFragment
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setFragment(HomeFragment())
        setListener()
    }

    private fun initViews() {
        mNav = findViewById(R.id.mNav)
        home = HomeFragment()
        user = UserFragmnet()
        notification = NotificationFragment()
        firebaseAuth = FirebaseAuth.getInstance()
        progressbar = findViewById(R.id.mainPBar)
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

    private fun setFragment(fragment: Fragment) {
        var setFragObj: FragmentTransaction = supportFragmentManager.beginTransaction()
        setFragObj.replace(R.id.mFrame, fragment)
        setFragObj.commit()
    }
}
