package com.vchat.muhammadfaizan.vchat.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.vchat.muhammadfaizan.vchat.model.User_Profile_Data;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView txtUserName;
    private TextView txtUserPhone;
    private TextView txtUserGroup;
    private CircleImageView imgUserProfile;
    private DatabaseReference databaseReference;
    private ImageView editProfile;
    private String user_group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        getUserInfo(view);
        goToProfileSetitngs(view);
        return view;
    }

    private void initViews(@NotNull View view) {
        txtUserName = view.findViewById(R.id.txt_user_name);
        txtUserPhone = view.findViewById(R.id.txt_user_phone);
        txtUserGroup = view.findViewById(R.id.txt_user_group);
        imgUserProfile = view.findViewById(R.id.img_user_image);
        editProfile = view.findViewById(R.id.img_edit_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
    }

    private void getUserInfo(View view) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User_Profile_Data obj = dataSnapshot.getValue(User_Profile_Data.class);
                txtUserName.setText(obj.getUser_Name());
                txtUserPhone.setText(obj.getPhone_Number());
                txtUserGroup.setText(obj.getGroup());
                user_group = obj.getGroup();
                Picasso.get().load(obj.getProfile_Image()).into(imgUserProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void goToProfileSetitngs(View view){
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), ActivityProfileSettings.class);
                intent.putExtra("group", user_group);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
