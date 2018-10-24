package com.vchat.muhammadfaizan.vchat.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vchat.muhammadfaizan.vchat.R;

import java.util.HashMap;

public class ActivitySignup extends AppCompatActivity {

    private Button btnRegister;
    private Button goToLogIn;
    private TextInputLayout inputPass, inputPassReenter, inputEmail, inputEmailReenter;
    private TextInputEditText edtEmail, edtEmailReenter, edtPass, edtPassReenter;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        registerUser();
    }

    private void initViews() {
        btnRegister = findViewById(R.id.btnCreateAccount);
        goToLogIn = findViewById(R.id.btnGoBack);

        inputPass = findViewById(R.id.inputPass);
        edtPass = findViewById(R.id.txtPass);

        inputPassReenter = findViewById(R.id.inputPassConfirm);
        edtPassReenter = findViewById(R.id.edtReenterPass);

        inputEmail = findViewById(R.id.inputEmailConfirm);
        edtEmail = findViewById(R.id.edtEnterEmail);
        edtEmail.requestFocus();

        inputEmailReenter = findViewById(R.id.inputEmail);
        edtEmailReenter = findViewById(R.id.txtEmail);

        progressBar = findViewById(R.id.pBarRegister);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void registerUser() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkErrorEnabled();
                if (!checkFields()){
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (edtEmail.getText().toString().equals("") || edtEmailReenter.getText().toString().equals("") || edtPass.getText().toString().equals("") || edtPassReenter.getText().toString().equals("")) {
                    if (edtEmail.getText().toString().equals("")) {
                        inputEmail.setError("Cannot be empty!");
                        inputEmail.setErrorEnabled(true);
                    }
                    if (edtEmailReenter.getText().toString().equals("")) {
                        inputEmailReenter.setError("Cannot be empty!");
                        inputEmailReenter.setErrorEnabled(true);
                    }
                    if (edtPass.getText().toString().equals("")) {
                        inputPass.setError("Cannot be empty!");
                        inputPass.setErrorEnabled(true);
                    }
                    if (edtPassReenter.getText().toString().equals("")) {
                        inputPassReenter.setError("Cannot be empty!");
                        inputPassReenter.setErrorEnabled(true);
                    }
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, String> mMap = new HashMap<>();
                                mMap.put("id", firebaseAuth.getUid());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getUid()).setValue(mMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(ActivitySignup.this, MainActivity.class));
                                        finish();
                                    }
                                });

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ActivitySignup.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        goToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivitySignup.this, ActivityLogin.class));
                ActivitySignup.this.finish();
            }
        });
    }

    private void checkErrorEnabled() {
        if (!edtEmail.getText().toString().equals("") && inputEmail.isErrorEnabled()) {
            inputEmail.setErrorEnabled(false);
        }

        if (!edtEmailReenter.getText().toString().equals("") && inputEmailReenter.isErrorEnabled()) {
            inputEmailReenter.setErrorEnabled(false);
        }

        if (!edtPass.getText().toString().equals("") && inputPass.isErrorEnabled()) {
            inputPass.setErrorEnabled(false);
        }

        if (!edtPassReenter.getText().toString().equals("") && inputPassReenter.isErrorEnabled()) {
            inputPassReenter.setErrorEnabled(false);
        }
    }

    private boolean checkFields() {
        if (!edtEmail.getText().toString().equals(edtEmailReenter.getText().toString())) {
            Toast.makeText(ActivitySignup.this, "Fields do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!edtPass.getText().toString().equals(edtPassReenter.getText().toString())) {
            Toast.makeText(ActivitySignup.this, "Fields do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtEmail.getText().toString().equals(edtEmailReenter.getText().toString()) && edtPass.getText().toString().equals(edtPassReenter.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }
}
