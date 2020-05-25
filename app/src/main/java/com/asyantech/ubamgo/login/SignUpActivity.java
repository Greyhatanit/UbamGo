package com.asyantech.ubamgo.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.DashboardActivity;
import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    EditText et_name, et_address, et_phone, et_email, et_password;
    Button register;
    ProgressBar progressBar;

    TextView loginTextView;
    ImageView ubamProfile;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Uri filePath;
    FirebaseStorage firebaseStorage;

    //Profile Picture Url
    String profile_image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

        et_name = (EditText) findViewById(R.id.name);
        et_address = (EditText) findViewById(R.id.address);
        et_phone = (EditText) findViewById(R.id.phone);
        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);

        loginTextView = (TextView) findViewById(R.id.back_to_login);

        register=(Button) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //Upload Profile Picture
        ubamProfile = (ImageView) findViewById(R.id.ubam_profile);
        ubamProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1);
                 */
                CropImage.activity()
                        .start(SignUpActivity.this);
            }
        });

        //check if the user is logged in
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting data from the Sign Up UI and storing in value
                final String name = et_name.getText().toString().trim();
                final String address = et_address.getText().toString().trim();
                final String phone = et_phone.getText().toString().trim();
                final String email = et_email.getText().toString().trim();
                final String password = et_password.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(SignUpActivity.this, R.string.enter_name, Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(address)){
                    Toast.makeText(SignUpActivity.this, R.string.enter_address, Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(phone) && phone.length() < 10){
                    Toast.makeText(SignUpActivity.this, R.string.enter_phone_no, Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUpActivity.this, R.string.enter_email, Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpActivity.this, R.string.enter_password, Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(SignUpActivity.this, R.string.password_length, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                //Send Verification Email to User
                                currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, R.string.signup_verification_email, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, R.string.signup_error_verification_email + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                User user_details;
                                //checking if profile picture is uploaded
                                if(profile_image_url.isEmpty()){
                                    Toast.makeText(SignUpActivity.this, "Profile Url is "+profile_image_url, Toast.LENGTH_SHORT).show();
                                    user_details = new User(currentUser.getUid(),null,name, address, phone, email);
                                }else{
                                    user_details = new User(currentUser.getUid(),profile_image_url,name, address, phone, email);
                                    Toast.makeText(SignUpActivity.this, "Profile Url is "+profile_image_url, Toast.LENGTH_SHORT).show();
                                }

                                //Uploading User Object to FireStore
                                DocumentReference documentReference = firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                                documentReference.set(user_details)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, R.string.signup_successful, Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                            progressBar.setVisibility(View.GONE);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, R.string.signup_error+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                            } else {
                                Toast.makeText(SignUpActivity.this, R.string.signup_unsuccessful,
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //ubamProfile.setImageBitmap(bitmap);
                ubamProfile.setImageURI(filePath);
                uploadImage(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        /*
        if(requestCode == 1 && resultCode == RESULT_OK && data!= null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ubamProfile.setImageBitmap(bitmap);
                uploadImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         */
    }


    private void uploadImage(final Uri imageUri){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.uploading);
        progressDialog.show();

        String profile_path = "profiles/"+UUID.randomUUID().toString();
        final StorageReference reference = firebaseStorage.getReference(profile_path);
        final UploadTask uploadTask = reference.putFile(imageUri);
        //upload file to firestore
        uploadTask.addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
            progressDialog.dismiss();
            //get Download uri
            Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
                    }
                }
            );
            getDownloadUriTask.addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        profile_image_url = downloadUri.toString();
                    }
                }
            });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(R.string.uploading +"\u0020 \u0020"+(int)progress + " %");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, R.string.profile_upload_unsuccessful, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
