package com.vchat.muhammadfaizan.vchat.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vchat.muhammadfaizan.vchat.R;
import com.vchat.muhammadfaizan.vchat.model.User_Fragment_Data;
import com.vchat.muhammadfaizan.vchat.controller.Users_List_Adapter;

import java.util.ArrayList;
import java.util.List;

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
        recyclerView = view.findViewById(R.id.userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        preferences = view.getContext().getSharedPreferences("user_group", Context.MODE_PRIVATE);
        group = preferences.getString("group_name", "null");
        groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(group).child("Members");
    }

    private void setAdapter(View view){
        final List<User_Fragment_Data> mList = new ArrayList<User_Fragment_Data>();
        final Users_List_Adapter adapter = new Users_List_Adapter(mList, view.getContext());
        recyclerView.setAdapter(adapter);
        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User_Fragment_Data obj = dataSnapshot.getValue(User_Fragment_Data.class);
                mList.add(obj);
                Log.i("demo_info", obj.getUser_Name());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
