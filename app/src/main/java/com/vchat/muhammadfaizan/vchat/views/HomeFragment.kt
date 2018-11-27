package com.vchat.muhammadfaizan.vchat.views


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.vchat.muhammadfaizan.vchat.R
import com.vchat.muhammadfaizan.vchat.controller.Message_Adapter
import com.vchat.muhammadfaizan.vchat.model.User_Message_Data

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var editText: EditText
    lateinit var imageView: ImageView
    lateinit var message : String
    var databaseReference: DatabaseReference? = null
    lateinit var list: MutableList<User_Message_Data>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        sendUserMessage(view)
        prepareList(view)
        return view
    }

   private fun initViews(view : View){
       recyclerView = view.findViewById(R.id.message_list)
       editText = view.findViewById(R.id.edt_message)
       imageView = view.findViewById(R.id.btn_send_message)
       databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(getUserGroup(view)).child("Chat")
       list = ArrayList<User_Message_Data>()
   }

    private fun getUserGroup(view : View) : String {
        var pref = view.context.getSharedPreferences("user_group", Context.MODE_PRIVATE)
        return pref.getString("group_name", "none")
    }

    private fun prepareList(view : View){
        setLayoutManager(view)
        var adapter = Message_Adapter(list, view.context)
        recyclerView.adapter = adapter
        try {
            databaseReference?.addChildEventListener(object : ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    var obj : User_Message_Data = p0.getValue(User_Message_Data::class.java)!!
                    list.add(obj)
                    adapter.notifyDataSetChanged()
                    adapter.notifyItemInserted(list.size+1)
                    setLayoutManager(view)
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }
            })
        } catch (e : Exception) {
            Toast.makeText(view.context, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun sendUserMessage(view : View){
        imageView.setOnClickListener {
            if (!editText.text.toString().equals("") && editText.text.toString() != null) {
                message = editText.text.toString().trim()
                var map = HashMap<String, String>()
                var uri : Uri? = FirebaseAuth.getInstance().currentUser!!.photoUrl
                Log.i("image_uri", uri.toString())
                map["Sender_Name"] = FirebaseAuth.getInstance().currentUser!!.displayName.toString()
                map["Sender_Key"] = FirebaseAuth.getInstance().uid!!.toString()
                map["Sender_Image"] = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
                map["Message"] = message
                databaseReference!!.push().setValue(map).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(view.context, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
                editText.setText("")
            } else {
                editText.error = "Cannot be empty"
            }
        }
    }

    private fun setLayoutManager(view : View){
        var layout_manager = LinearLayoutManager(view.context)
        layout_manager.stackFromEnd = true
        recyclerView.layoutManager = layout_manager
    }
}
