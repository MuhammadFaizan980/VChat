package com.vchat.muhammadfaizan.vchat.views

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
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
        checkEmail()
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

    private fun checkEmail() {

        if (intent.extras.get("provider").equals("email")) {
            if (!firebaseAuth.currentUser!!.isEmailVerified) {
                var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle("Warning!")
                dialog.setMessage("Your email is not verified\nPlease verify your email by clicking Verify button")
                dialog.setPositiveButton("Verify", DialogInterface.OnClickListener { dialogInterface, i ->
                    progressbar.visibility = View.VISIBLE
                    firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            progressbar.visibility = View.INVISIBLE
                            Toast.makeText(applicationContext, "Verification link sent to your email\nPlease verify as soon as possible", Toast.LENGTH_LONG).show()
                            dialogInterface.cancel()
                        } else {
                            progressbar.visibility = View.INVISIBLE
                            Toast.makeText(applicationContext, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                            dialogInterface.cancel()
                        }
                    }
                }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })

                dialog.show()
            }
        }
    }
}
