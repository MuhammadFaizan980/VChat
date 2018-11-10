package com.vchat.muhammadfaizan.vchat.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vchat.muhammadfaizan.vchat.R;
import com.vchat.muhammadfaizan.vchat.model.User_Fragment_Data;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentUsers extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference groupRef;
    String group;
    SharedPreferences preferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        initViews(view);
        setAdapter(view);
        return view;
    }

    private void initViews(View view){
        groupRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Group");
        recyclerView = view.findViewById(R.id.userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        preferences = view.getContext().getSharedPreferences("user_group", Context.MODE_PRIVATE);
        group = preferences.getString("group_name", "null");
    }

    private void setAdapter(View view){
        List<User_Fragment_Data> mList = new ArrayList<User_Fragment_Data>();
    }

}
