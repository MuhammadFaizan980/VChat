package com.vchat.muhammadfaizan.vchat.controller

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vchat.muhammadfaizan.vchat.R
import com.vchat.muhammadfaizan.vchat.model.User_Message_Data

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class holder(view : View) : RecyclerView.ViewHolder(view) {

    }
}