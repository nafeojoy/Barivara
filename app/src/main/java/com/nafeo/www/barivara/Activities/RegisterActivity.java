package com.nafeo.www.barivara.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nafeo.www.barivara.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mUserName;
    private EditText mMobileNo;

    private TextView mRegisterBtn;
    private TextView mGotoLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;

    private ProgressDialog mProgress;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    //Control Declarations...
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mUserName = (EditText) findViewById(R.id.userName);
        mMobileNo = (EditText) findViewById(R.id.mobileNo);
        mRegisterBtn = (TextView) findViewById(R.id.registerBtn);
        mGotoLogin = (TextView) findViewById(R.id.logiin);

        mGotoLogin.setOnClickListener(this);
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            }
        };

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegistration();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startRegistration(){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        final String name = mUserName.getText().toString();
        final String mobile = mMobileNo.getText().toString();


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobile)){

            mProgress.setMessage("Registering");
            mProgress.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            String user_id = mAuth.getCurrentUser().getUid();

                            mDatabaseUsers.child(user_id);

                            DatabaseReference current_user_db = mDatabaseUsers.child(user_id);

                            current_user_db.child("name").setValue(name);
                            current_user_db.child("mobileno").setValue(mobile);

                            mProgress.dismiss();

                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }

                    }
                });
            }

        else {
            Toast.makeText(RegisterActivity.this, "Empty Fields", Toast.LENGTH_LONG).show();
        }
    }

    private void checkUserExists(){

        final String name = mUserName.getText().toString();
        final String mobile = mMobileNo.getText().toString();

        int x;


        //From Database (Working)
        mDatabaseUsers.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try{
                            String y = dataSnapshot.getValue().toString(); // Reads all the Objects with name == "nafeo"
                            Toast.makeText(RegisterActivity.this, y, Toast.LENGTH_LONG).show();


                        } catch (Exception ex) {
                            Toast.makeText(RegisterActivity.this, "No Value", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.logiin){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}
