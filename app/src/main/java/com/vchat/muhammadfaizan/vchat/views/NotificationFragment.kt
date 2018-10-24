package com.vchat.muhammadfaizan.vchat.views


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.vchat.muhammadfaizan.vchat.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NotificationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view: View = inflater.inflate(R.layout.fragment_notification, container, false);
        var btn : Button = view.findViewById(R.id.btnNotifications)
        btn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(view.context, "Notification Clicked", Toast.LENGTH_LONG).show()
            }

        })
        return view
    }
}
