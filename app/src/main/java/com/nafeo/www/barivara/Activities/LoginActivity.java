package com.nafeo.www.barivara.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nafeo.www.barivara.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserNameField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private TextView mGotoRegister;

    private TextView mLoginBtn;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabase;


    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mGotoRegister = (TextView) findViewById(R.id.registerBtn);
        mGotoRegister.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mProgress = new ProgressDialog(this);

       // mUserNameField = (EditText) findViewById(R.id.userName);
        mEmailField = (EditText) findViewById(R.id.userEmail);
        mPasswordField = (EditText) findViewById(R.id.password);

        mLoginBtn = (TextView) findViewById(R.id.login);

       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null)

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));

            }
        };*/


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListener);
    }

    private void startLogin(){

        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Logging In...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        mProgress.dismiss();
                        checkuserExist();
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(LoginActivity.this, "Empty Fields", Toast.LENGTH_LONG).show();
        }

    }

    private void checkuserExist(){

        //From Auth
        final String user_id = mAuth.getCurrentUser().getUid();

        //From Database (Working)
        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String x = dataSnapshot.getValue().toString();

                    if(dataSnapshot.hasChild(user_id)){
                        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);

                        Toast.makeText(LoginActivity.this, x, Toast.LENGTH_LONG).show();
                    } else {
                        Intent homeIntent = new Intent(LoginActivity.this, AccountSetupActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerBtn) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
