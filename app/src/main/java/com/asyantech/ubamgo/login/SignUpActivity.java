package com.asyantech.ubamgo.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    EditText et_name, et_address, et_phone, et_email, et_password;
    Button register;
    ProgressBar progressBar;

    TextView loginTextView;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
                    Toast.makeText(SignUpActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(address)){
                    Toast.makeText(SignUpActivity.this, "Enter Address", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(SignUpActivity.this, "Enter Phone & should be 10 digits", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUpActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
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
                                User user_details = new User(currentUser.getUid(),null,name, address, phone, email);
                                // Add a new document with a generated ID
                                notebookRef.add(user_details)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(SignUpActivity.this, "Uploaded with DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "Error ! Message is: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            } else {
                                Toast.makeText(SignUpActivity.this, "User Upload Failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}
