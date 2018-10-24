package com.vchat.muhammadfaizan.vchat.views

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.vchat.muhammadfaizan.vchat.R

class ActivityLogin : AppCompatActivity() {
    lateinit var txtInputEmail: TextInputLayout
    lateinit var txtInputPass: TextInputLayout
    lateinit var txtEmail: TextInputEditText
    lateinit var txtPass: TextInputEditText
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    lateinit var progressBar: ProgressBar
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
        loginUser()
    }

    fun initViews() {
        txtInputEmail = findViewById(R.id.inputEmail)
        txtInputPass = findViewById(R.id.inputPass)
        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPass)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.pBarLogin)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }

    fun loginUser() {

        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var email: String = txtEmail.text.toString().trim()
                var pass: String = txtPass.text.toString()
                checkErrorEnabled()
                if (email.equals("") || pass.equals("")) {
                    if (email.equals("")) {
                        txtInputEmail.error = "Cannot be empty!"
                        txtInputEmail.setErrorEnabled(true)
                    }
                    if (pass.equals("")) {
                        txtInputPass.error = "Cannot be empty!"
                        txtInputPass.isErrorEnabled = true
                    }
                } else {
                    progressBar.visibility = View.VISIBLE
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            progressBar.visibility = View.INVISIBLE
                            if (p0.isSuccessful) {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                            } else {
                                Toast.makeText(applicationContext, p0.exception?.message.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }

        })
        btnRegister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext, ActivitySignup::class.java))
                finish()
            }
        })
    }

    fun checkErrorEnabled() {
        if (!txtEmail.text.toString().trim().equals("") && txtInputEmail.isErrorEnabled == true) {
            txtInputEmail.isErrorEnabled = false
        }

        if (!txtPass.text.toString().equals("") || txtInputPass.isErrorEnabled == true) {
            txtInputPass.isErrorEnabled = false
        }

    }
}
