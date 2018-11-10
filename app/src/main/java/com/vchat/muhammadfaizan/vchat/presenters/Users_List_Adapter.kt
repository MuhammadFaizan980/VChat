package com.vchat.muhammadfaizan.vchat.presenters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.vchat.muhammadfaizan.vchat.R
import com.vchat.muhammadfaizan.vchat.model.User_Fragment_Data
import de.hdodenhof.circleimageview.CircleImageView

class Users_List_Adapter(mList : List<User_Fragment_Data>, context : Context) : RecyclerView.Adapter<Users_List_Adapter.mHolder>() {

    var myList = mList
    var mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.users_row_design, parent, false)
        return mHolder(view)
    }

    override fun onBindViewHolder(holder: mHolder, position: Int) {
        var obj : User_Fragment_Data = myList[position];
        holder.txtUserName.text = obj.User_Name
        holder.txtUserPhone.text = obj.Phone_Number
        Picasso.get().load(obj.Profile_Image).into(holder.imgUser)
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class mHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imgUser : CircleImageView = itemView.findViewById(R.id.userImage)
        var txtUserName : TextView = itemView.findViewById(R.id.txtUserName)
        var txtUserPhone : TextView = itemView.findViewById(R.id.txtUserPhone)
    }
}
