package com.vchat.muhammadfaizan.vchat.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
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
    var isPermissionGranted = false
    var arr = arrayOfNulls<String>(4)
    var uri: Uri? = null
    private var REQUEST_CODE: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        initViews()
        checkUserCredentials(firebaseAuth)
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
                progressBar.visibility = View.VISIBLE
                var uploadTask: UploadTask = storageRef.putFile(uri!!);
                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation storageRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        var map = HashMap<String, String>()
                        map["User_ID"] = firebaseAuth.uid.toString()
                        map["User_Name"] = edtFirstName.text.toString() + " " + edtLastName.text.toString()
                        map["Phone_Number"] = edtPhoneNumber.text.toString()
                        map["Profile_Image"] = downloadUri.toString()
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
                                    var locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                    var location: Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                    var mMap = HashMap<String, String>()
                                    mMap["Latitude"] = location.latitude.toString()
                                    mMap["Longitude"] = location.longitude.toString()
                                    dbRef.updateChildren(mMap as Map<String, String>).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            var profileChangeRequest = UserProfileChangeRequest.Builder()
                                                    .setDisplayName(edtFirstName.text.toString() + " " + edtLastName.text.toString())
                                                    .setPhotoUri(uri).build()
                                            firebaseAuth.currentUser!!.updateProfile(profileChangeRequest).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    progressBar.visibility = View.INVISIBLE
                                                    startActivity(Intent(applicationContext, MainActivity::class.java))
                                                    finish()
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
            if (grantResults.size > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this as Activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    isPermissionGranted = true
                }
            }
        }
    }

    fun checkUserCredentials(auth : FirebaseAuth){
        if (auth.currentUser!!.displayName != null) {
            startActivity(Intent(this@ActivityProfileSettings, MainActivity::class.java))
            finish()
        }
    }
}
