package com.vchat.muhammadfaizan.vchat.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vchat.muhammadfaizan.vchat.R
import de.hdodenhof.circleimageview.CircleImageView

class ActivityProfileSettings : AppCompatActivity() {

    lateinit var spinner: Spinner
    lateinit var edtFirstName: EditText
    lateinit var edtLastName: EditText
    lateinit var edtPhoneNumber: EditText
    lateinit var imgUser: CircleImageView
    lateinit var btnSave: Button
    lateinit var progressBar: ProgressBar
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var storageRef: StorageReference
    lateinit var group: String
    lateinit var firstName: String
    lateinit var lastname: String
    lateinit var phone: String
    var arr = arrayOfNulls<String>(4)
    var uri: Uri? = null
    private var REQUEST_CODE: Int = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        initViews()
        prepareSpinner()
        getUserImageURI()
        setListener()
    }

    fun initViews() {
        spinner = findViewById(R.id.spinnerGroup)
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        imgUser = findViewById(R.id.imgUserProfile)
        btnSave = findViewById(R.id.btnSaveProfile)
        progressBar = findViewById(R.id.progressSettings)
        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("Chat_Images/" + System.currentTimeMillis() + ".jpg")
    }

    fun prepareSpinner() {
        arr[0] = "---Select Group---"
        arr[1] = "Group-01"
        arr[2] = "Group-02"
        arr[3] = "Group-03"

        var adapter = ArrayAdapter<String>(this, R.layout.spinner_row, R.id.txtSpinnerItem, arr)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                group = arr[p2].toString()
            }
        }

    }

    fun getUserImageURI() {
        imgUser.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
            imgUser.setImageURI(uri)
        }
    }

    fun setListener() {
        btnSave.setOnClickListener {
            if (uri != null &&
                    !group.equals(arr[0]) &&
                    !edtFirstName.text.toString().equals("") &&
                    !edtLastName.text.toString().equals("") &&
                    !edtPhoneNumber.text.toString().equals("")) {

            } else {
                Toast.makeText(this, "Fill all fields first and select a group", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        progressBar.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.INVISIBLE
    }
}
