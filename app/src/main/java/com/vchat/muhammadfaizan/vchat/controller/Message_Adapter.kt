package com.vchat.muhammadfaizan.vchat.controller

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.vchat.muhammadfaizan.vchat.R
import com.vchat.muhammadfaizan.vchat.model.User_Message_Data
import de.hdodenhof.circleimageview.CircleImageView

class Message_Adapter constructor(mList : List<User_Message_Data>, mContext : Context) : RecyclerView.Adapter<Message_Adapter.holder>() {

    var list = mList
    var context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        var inflater = LayoutInflater.from(parent.context)
        var view : View = inflater.inflate(R.layout.message_row_design, parent, false)
        return holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: holder, position: Int) {

        var obj : User_Message_Data = list.get(position)
        Picasso.get().load(obj.Sender_Image.toString()).into(holder.imgSender)
        holder.txtUserName.text = obj.Sender_Name
        holder.txtMessage.text = obj.Message

        if (obj.Sender_Key.equals(FirebaseAuth.getInstance().uid)) {
            holder.layout.gravity = Gravity.END
        }
    }

    inner class holder(view : View) : RecyclerView.ViewHolder(view) {
        var imgSender : CircleImageView = view.findViewById(R.id.imgSender)
        var txtUserName : TextView = view.findViewById(R.id.txtSenderName)
        var txtMessage : TextView = view.findViewById(R.id.txtMessage)
        var layout : LinearLayout = view.findViewById(R.id.message_parent_layout)
    }
}