package com.vchat.muhammadfaizan.vchat.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vchat.muhammadfaizan.vchat.R;
import com.vchat.muhammadfaizan.vchat.model.User_Fragment_Data;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView txtUserName;
    private TextView txtUserPhone;
    private TextView txtUserGroup;
    private CircleImageView imgUserProfile;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        getUserInfo(view);
        return view;
    }

    private void initViews(@NotNull View view) {
        txtUserName = view.findViewById(R.id.txt_user_name);
        txtUserPhone = view.findViewById(R.id.txt_user_phone);
        txtUserGroup = view.findViewById(R.id.txt_user_group);
        imgUserProfile = view.findViewById(R.id.img_user_image);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
    }

    private void getUserInfo(@NotNull View view) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
