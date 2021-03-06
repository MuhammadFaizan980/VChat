package com.vchat.muhammadfaizan.vchat.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
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
    lateinit var dbRef: DatabaseReference
    lateinit var group: String
    lateinit var location: Location
    var isPermissionGranted = false
    var arr = arrayOfNulls<String>(4)
    var uri: Uri? = null
    var download_uri : Uri? = null
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
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.uid!!)
        storageRef = FirebaseStorage.getInstance().getReference("Profile_Images/" + firebaseAuth.uid + ".jpg")
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
                var preferences = getSharedPreferences("user_group", Context.MODE_PRIVATE)
                var editor = preferences.edit()
                editor.putString("group_name", group)
                editor.apply()
                progressBar.visibility = View.VISIBLE
                var uploadTask: UploadTask = storageRef.putFile(uri!!)
                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation storageRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        download_uri = task.result
                        var map = HashMap<String, String>()
                        map["User_ID"] = firebaseAuth.uid.toString()
                        map["User_Name"] = edtFirstName.text.toString().trim() + " " + edtLastName.text.toString().trim()
                        map["Phone_Number"] = edtPhoneNumber.text.toString()
                        map["Profile_Image"] = download_uri.toString()
                        map["Group"] = group
                        dbRef.setValue(map).addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(p0: Task<Void>) {
                                if (p0.isSuccessful) {
                                    if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                            ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        var permissionArray = arrayOfNulls<String>(2)
                                        permissionArray[0] = android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        permissionArray[1] = android.Manifest.permission.ACCESS_FINE_LOCATION
                                        ActivityCompat.requestPermissions(this@ActivityProfileSettings, permissionArray, 9)
                                    }
                                    var mMap = HashMap<String, String>()
                                    var locationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
                                    var task = locationProvider.lastLocation
                                    task.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            location = task.result!!
                                            mMap["Longitude"] = location.longitude.toString()
                                            mMap["Latitude"] = location.latitude.toString()
                                            dbRef.updateChildren(mMap as Map<String, String>).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    var profileChangeRequest = UserProfileChangeRequest.Builder()
                                                            .setDisplayName(edtFirstName.text.toString() + " " + edtLastName.text.toString())
                                                            .setPhotoUri(download_uri).build()
                                                    firebaseAuth.currentUser!!.updateProfile(profileChangeRequest).addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            Log.i("image_uri", uri.toString())
                                                            Log.i("image_uri", firebaseAuth.currentUser!!.photoUrl.toString())
                                                            var groupProfileData = HashMap<String, String>();
                                                            groupProfileData["User_ID"] = firebaseAuth.uid.toString()
                                                            groupProfileData["User_Name"] = edtFirstName.text.toString().trim() + " " + edtLastName.text.toString().trim()
                                                            groupProfileData["Phone_Number"] = edtPhoneNumber.text.toString()
                                                            groupProfileData["Profile_Image"] = download_uri.toString()
                                                            groupProfileData["Latitude"] = location.latitude.toString()
                                                            groupProfileData["Longitude"] = location.longitude.toString()
                                                            FirebaseDatabase.getInstance().getReference("Groups").child(group).child("Members").child(firebaseAuth.uid!!).setValue(groupProfileData).addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    progressBar.visibility = View.INVISIBLE
                                                                    if (intent.extras.getString("group") != null && intent.extras.get("group")!= group) {
                                                                        FirebaseDatabase.getInstance().getReference("Groups").child(intent.extras.getString("group")).child("Members").child(firebaseAuth.uid.toString()).setValue(null).addOnCompleteListener { task ->
                                                                            startActivity(Intent(this@ActivityProfileSettings, MainActivity::class.java))
                                                                            this@ActivityProfileSettings.finish()
                                                                        }
                                                                    } else {
                                                                        startActivity(Intent(this@ActivityProfileSettings, MainActivity::class.java))
                                                                        this@ActivityProfileSettings.finish()
                                                                    }
                                                                } else {
                                                                    progressBar.visibility = View.INVISIBLE
                                                                    Toast.makeText(this@ActivityProfileSettings, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                                                                }
                                                            }
                                                        } else {
                                                            progressBar.visibility = View.INVISIBLE
                                                            Toast.makeText(applicationContext, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                                                        }
                                                    }
                                                } else {
                                                    progressBar.visibility = View.INVISIBLE
                                                    Toast.makeText(applicationContext, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                                                }
                                            }

                                        }
                                    }

                                } else {
                                    progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(applicationContext, p0.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                    } else {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 9) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this as Activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    isPermissionGranted = true
                }
            }
        }
    }
}
