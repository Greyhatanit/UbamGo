package com.asyantech.ubamgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.login.LoginActivity;
import com.asyantech.ubamgo.seatbooking.SeatBookingActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    ImageView profileImageView;
    TextView profileTextView;
    TextView profileEmailTextView;

    Button btn_verify;
    LinearLayout verify_email_layout;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;
    private Uri filePath;
    String userID;

    //Swipe Refresh Layout
    SwipeRefreshLayout swipeRefreshLayout;

    //Firebase login provider
    String firebaseSignInProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SeatBookingActivity.class);
                startActivity(intent);
            }
        });
         */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_gallery, R.id.nav_slideshow, R.id.notificationFragment, R.id.inviteEarnFragment,
                R.id.settingFragment, R.id.homeFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_item);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        verify_email_layout = findViewById(R.id.verfiyEmailLayout);
        btn_verify = (Button) findViewById(R.id.btn_verify_email);

        firebaseUser = firebaseAuth.getCurrentUser();
        /*
        firebaseSignInProvider = (String) firebaseUser.getIdToken(false).getResult().getSignInProvider();
        if(firebaseSignInProvider.equals("facebook.com") || firebaseSignInProvider.equals("google.com")){
            verify_email_layout.setVisibility(View.GONE);
        }else{
            if(!firebaseUser.isEmailVerified()){
                verify_email_layout.setVisibility(View.VISIBLE);
                btn_verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), R.string.signup_verification_email, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), R.string.signup_error_verification_email + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }else{
                verify_email_layout.setVisibility(View.GONE);
            }
        }
         */

        View navHeaderView = navigationView.getHeaderView(0);
        //Set Images in NavigationDrawer
        profileImageView = (ImageView) navHeaderView.findViewById(R.id.profileImageView);
        profileTextView = (TextView) navHeaderView.findViewById(R.id.profileTextView);
        profileEmailTextView = (TextView) navHeaderView.findViewById(R.id.profileEmailTextView);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery
                /*
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1);
                 */
                CropImage.activity()
                        .start(DashboardActivity.this);
            }
        });

        loadUserInformation();

        //Swipe Refreshlayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutDashboard);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                String firebaseSignInProvider = (String) firebaseUser.getIdToken(false).getResult().getSignInProvider();
                if(firebaseSignInProvider.equals("facebook.com") || firebaseSignInProvider.equals("google.com")){
                    verify_email_layout.setVisibility(View.GONE);
                }else{
                    verify_email_layout.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadUserInformation(){
        userID = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = firestore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            String image_url = documentSnapshot.getString("user_img");
            if(image_url != null){
                Glide.with(getApplicationContext())
                        .load(image_url)
                        .into(profileImageView);
            }else{
                profileImageView.setImageResource(R.drawable.placeholder_for_profile);
            }
            profileTextView.setText(documentSnapshot.getString("user_name"));
            profileEmailTextView.setText(documentSnapshot.getString("user_email"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                //Empty Shared Preference
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                //Logout of Firebase
                firebaseAuth.getInstance().signOut();
                updateUI();
                break;

            case R.id.action_change_password:
                changePassword();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        /*
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null){
            updateUI();
        }
         */
        //Check if the user has logged in from facebook
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            //Error in this line too
            String firebaseSignInProvider = (String) firebaseUser.getIdToken(false).getResult().getSignInProvider();
            if(firebaseSignInProvider.equals("facebook.com") || firebaseSignInProvider.equals("google.com")){
                verify_email_layout.setVisibility(View.GONE);
            }else{
                if(!firebaseUser.isEmailVerified()){
                    verify_email_layout.setVisibility(View.VISIBLE);
                    btn_verify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), R.string.signup_verification_email, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), R.string.signup_error_verification_email + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else{
                    verify_email_layout.setVisibility(View.GONE);
                }
            }
        }
    }

    private void updateUI() {
        Toast.makeText(this, R.string.logout, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    void changePassword(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DashboardActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.reset_password_dialog, null);

        final EditText reset_password = (EditText)mView.findViewById(R.id.et_reset_password);
        final ProgressBar progressBarResetPassword = (ProgressBar) mView.findViewById(R.id.progressBarResetPassword);
        Button btn_reset_password = (Button) mView.findViewById(R.id.btn_reset_password);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String forgottenEmail = forgotten_email.getText().toString().trim();
                String password_typed = reset_password.getText().toString().trim();
                if (reset_password.getText().toString().isEmpty() && password_typed.length() < 6) {
                    Toast.makeText(DashboardActivity.this, R.string.password_length, Toast.LENGTH_LONG).show();
                    return;
                }
                //Sending password reset email to forgotten passwords
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                progressBarResetPassword.setVisibility(View.VISIBLE);
                firebaseUser.updatePassword(password_typed)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBarResetPassword.setVisibility(View.GONE);
                            dialog.dismiss();
                            Toast.makeText(DashboardActivity.this, R.string.password_reset_successful, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarResetPassword.setVisibility(View.GONE);
                            dialog.dismiss();
                            Toast.makeText(DashboardActivity.this, R.string.password_reset_unsuccessful +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if(requestCode == 1 && resultCode == RESULT_OK && data!= null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);
                uploadImageToFirebase(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } */

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                profileImageView.setImageURI(filePath);
                uploadImageToFirebase(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void uploadImageToFirebase(final Uri imageUri){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.uploading);
        progressDialog.show();

        String profile_path = "profiles/"+UUID.randomUUID().toString();
        final StorageReference reference = firebaseStorage.getReference(profile_path);
        final UploadTask uploadTask = reference.putFile(imageUri);
        //upload file to firestore
        uploadTask.addOnCompleteListener(DashboardActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                getDownloadUriTask.addOnCompleteListener(DashboardActivity.this, new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        //Key to upload
                        final String KEY_ProfileImage = "user_img";
                        String profile_image_url = downloadUri.toString();
                        //update the user_img section of the corresponding user
                        Map<String, Object> note = new HashMap<>();
                        note.put(KEY_ProfileImage, profile_image_url );
                        DocumentReference documentReference = firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                        documentReference.set(note, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(DashboardActivity.this, R.string.profile_upload_successful, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DashboardActivity.this, R.string.profile_upload_unsuccessful, Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(R.string.uploading +"\u0020 \u0020"+ (int)progress + " %");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DashboardActivity.this, R.string.profile_upload_unsuccessful, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
