package com.nafeo.www.barivara.Activities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nafeo.www.barivara.Classes.UserInformation;
import com.nafeo.www.barivara.Fragments.ImageUploadFragment;
import com.nafeo.www.barivara.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button mLogoutBtn;
    Button mUpdateBtn;

    private EditText mName;
    private EditText mUserName;
    private EditText mLocation;
    private EditText mPhoneNo;
    private EditText mFacebookId;
    private EditText mOccupation;

    private ProgressDialog mProgress;

    TextView mEditProfilePicture;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        //Controls
        mName = (EditText) findViewById(R.id.nameText);
        //mUserName = (EditText) findViewById(R.id.password);
        mLocation = (EditText) findViewById(R.id.locationText);
        mPhoneNo = (EditText) findViewById(R.id.phoneText);
        mFacebookId = (EditText) findViewById(R.id.facebookText);
        mOccupation = (EditText) findViewById(R.id.occupationText);

        mEditProfilePicture = (TextView) findViewById(R.id.EditProfilePicture);
        mEditProfilePicture.setOnClickListener(this);
        mLogoutBtn = (Button) findViewById(R.id.logoutBtn);
        mLogoutBtn.setOnClickListener(this);
        mUpdateBtn = (Button) findViewById(R.id.updateBtn);
        mUpdateBtn.setOnClickListener(this);

        //Progress
        mProgress = new ProgressDialog(this);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){

                    Intent loginActivity = new Intent(HomeActivity.this, LoginActivity.class);
                    loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginActivity);

                }

            }
        };

        Firebase.setAndroidContext(this);
        showData();

        //Bottom Navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onStart() {
        super.onStart();


        //mAuth.addAuthStateListener(mAuthListener);
        //checkuserExist();
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
                    Intent homeIntent = new Intent(HomeActivity.this, AccountSetupActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);

                    Toast.makeText(HomeActivity.this, x, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.logoutBtn){
            mAuth.getInstance().signOut();
            HomeActivity.this.finish();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);

        }
        if(v.getId() == R.id.EditProfilePicture){
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new ImageUploadFragment ()).commit();
        }
        if(v.getId() == R.id.updateBtn){
            updateProfile();
        }

    }

    private void updateProfile(){
        String name = mName.getText().toString();
        String location = mLocation.getText().toString();
        String phoneNo = mPhoneNo.getText().toString();
        String facebookId = mFacebookId.getText().toString();
        String occupation = mOccupation.getText().toString();



        mProgress.setMessage("Updating...");
        mProgress.show();

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabaseUsers.child(user_id);

            current_user_db.child("name").setValue(name);
            current_user_db.child("mobileno").setValue(phoneNo);
            current_user_db.child("location").setValue(location);
            current_user_db.child("facebookid").setValue(facebookId);
            current_user_db.child("occupation").setValue(occupation);

        mProgress.dismiss();




    }

    private void showData(){

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabaseUsers.child(user_id);

        current_user_db.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                mName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* String value = dataSnapshot.getValue(String.class);
        mOccupation.setText(value);

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserInformation userInfo = postSnapshot.getValue(UserInformation.class);

                    String name = userInfo.getName();
                    String location = userInfo.getLocation();
                    String phoneNo = userInfo.getMobile_no();
                    String facebookId = userInfo.getFacebook_id();
                    String occupation = userInfo.getOccupation();
                }*/
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent in;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    in=new Intent(getBaseContext(),HomeActivity.class);
                    startActivity(in);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.navigation_tanent:
                    in=new Intent(getBaseContext(),TanentActivity.class);
                    startActivity(in);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.navigation_landlord:
                    in=new Intent(getBaseContext(),LandlordActivity.class);
                    startActivity(in);
                    overridePendingTransition(0, 0);
                    break;
            }
            return false;
        }

    };
}
