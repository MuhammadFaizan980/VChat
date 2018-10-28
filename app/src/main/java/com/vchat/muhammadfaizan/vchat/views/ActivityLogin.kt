package com.vchat.muhammadfaizan.vchat.views

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
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
    lateinit var btnSignInGoogle: SignInButton
    lateinit var btnSignInFacebook: LoginButton
    lateinit var callbackManager: CallbackManager


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
        btnSignInGoogle = findViewById(R.id.btnGoogle)
        btnSignInFacebook = findViewById(R.id.btnFB)
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }

    fun loginUser() {
        //Facebook Login
        callbackManager = CallbackManager.Factory.create()

        btnSignInFacebook.setReadPermissions("email", "public_profile")
        btnSignInFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("tg", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
                progressBar.visibility = View.VISIBLE
            }

            override fun onCancel() {
                Log.d("tg", "facebook:onCancel")
                progressBar.visibility = View.INVISIBLE
                // ...
            }

            override fun onError(error: FacebookException) {
                progressBar.visibility = View.INVISIBLE
                Log.d("tg", "facebook:onError", error)
                // ...
            }
        })

        //Google Login
        btnSignInGoogle.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                progressBar.visibility = View.VISIBLE
                googleSignIn()
            }
        })

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

    fun googleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 2)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressBar.visibility = View.INVISIBLE
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 2) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(applicationContext, "Signin Error...", Toast.LENGTH_SHORT).show()
                // ...
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().uid!!).child("id").setValue(FirebaseAuth.getInstance().uid).addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful) {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(applicationContext, p0.exception?.message.toString(), Toast.LENGTH_LONG).show()
                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                    })


                } else {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(applicationContext, p0.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("tg", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().uid!!).child("id").setValue(FirebaseAuth.getInstance().uid).addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(p0: Task<Void>) {
                                if (p0.isSuccessful) {
                                    startActivity(Intent(applicationContext, MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, p0.exception?.message.toString(), Toast.LENGTH_LONG).show()
                                    progressBar.visibility = View.INVISIBLE
                                }
                            }
                        })

                    } else {
                        progressBar.visibility = View.INVISIBLE
                        // If sign in fails, display a message to the user.
                        Log.w("tg", "signInWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, task.exception?.message.toString(),
                                Toast.LENGTH_SHORT).show()
                    }

                }
    }
}
