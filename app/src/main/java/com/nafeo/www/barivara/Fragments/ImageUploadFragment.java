package com.nafeo.www.barivara.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nafeo.www.barivara.Activities.HomeActivity;
import com.nafeo.www.barivara.Classes.ImageUpload;
import com.nafeo.www.barivara.R;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageUploadFragment extends Fragment implements View.OnClickListener {


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseUserId;

    private FirebaseAuth mAuth;

    private Button uploadBtn;
    private Button browseBtn;
    private Button cancelBtn;
    private ImageView imageView;
    private EditText imageName;
    private Uri imageUri;

    public static final String FB_STORAGE_PATH = "image/profilepic/";
    public static final String FB_DATABASE_PATH = "image/profilepic";
    public static final int REQUEST_CODE = 1;




    public ImageUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View ImageUploadFragmentView = inflater.inflate(R.layout.fragment_image_upload, container, false);

        mAuth = FirebaseAuth.getInstance();

        cancelBtn = (Button) ImageUploadFragmentView.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);

        browseBtn = (Button) ImageUploadFragmentView.findViewById(R.id.browseBtn);
        browseBtn.setOnClickListener(this);

        uploadBtn = (Button) ImageUploadFragmentView.findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(this);

        imageView = (ImageView) ImageUploadFragmentView.findViewById(R.id.imageView);
        imageView.setOnClickListener(this);

        imageName = (EditText) ImageUploadFragmentView.findViewById(R.id.imageName);

        // Inflate the layout for this fragment
        return ImageUploadFragmentView;
    }




    @SuppressWarnings("VisibleForTests")
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cancelBtn){
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);

        }
        if(v.getId() == R.id.browseBtn){
            /*Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_CODE);

            Toast.makeText(getActivity().getApplicationContext(), "XYZ", Toast.LENGTH_SHORT).show();*/

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Image"),REQUEST_CODE );
        }
        if(v.getId() == R.id.uploadBtn){

            final String user_id = mAuth.getCurrentUser().getUid();



            mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            mDatabaseUserId = mDatabaseUsers.child(user_id);


            if(imageUri!= null){
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setTitle("Uploading Image...");
                dialog.show();

                StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() +"."+ getImageExt(imageUri));

                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Dismiss dialog when success
                        dialog.dismiss();

                        Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                        ImageUpload imageUpload = new ImageUpload(imageName.getText().toString(), taskSnapshot.getDownloadUrl().toString());

                        //save image info into firebase database
                        String uploadId = mDatabaseUserId.push().getKey();
                        mDatabaseUserId.child(uploadId).setValue(imageUpload);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Dismiss dialog when fail
                                dialog.dismiss();

                                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();




                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {


                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //show upload progress

                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded " + (int)progress+"");

                            }
                        });

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && requestCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}
