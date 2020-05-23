package com.asyantech.ubamgo.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asyantech.ubamgo.DashboardActivity;
import com.asyantech.ubamgo.R;
import com.asyantech.ubamgo.model.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.BuildConfig;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity{
    TextView go_to_signup, forgotPassword;
    EditText et_email, et_password;
    Button login;

    ProgressBar progressBar;
    String email, password;

    //Remember Me Function
    CheckBox remember;

    //Facebook login
    private CallbackManager callbackManager;
    private Button facebookLoginButton;
    AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;

    //Google Login
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;


    //Firestore
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = AccessToken.getCurrentAccessToken();
        loadLocale();
        setContentView(R.layout.activity_login);
        //Change the acttionbar title, if you dont change it will be according to your systems default langauge
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

        firebaseAuth = FirebaseAuth.getInstance();

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        login = (Button) findViewById(R.id.btn_login);

        //Remember Check Box
        remember = (CheckBox) findViewById(R.id.remember);
        //facebook login button
        facebookLoginButton = (Button) findViewById(R.id.facebook_login);

        //Function of SignUp Text View
        go_to_signup = (TextView) findViewById(R.id.go_to_signup);
        go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });

        //Forgot Password
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.forgot_password_dialog, null);
                final EditText forgotten_email = (EditText)mView.findViewById(R.id.et_forgotten_email);
                final ProgressBar progressBarForgotPassword = (ProgressBar) mView.findViewById(R.id.progressBarForgotPassword);
                Button btn_forgetten_password = (Button) mView.findViewById(R.id.btn_forgot_password);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btn_forgetten_password.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    String forgottenEmail = forgotten_email.getText().toString().trim();
                    if(forgotten_email.getText().toString().isEmpty()){
                        Toast.makeText(LoginActivity.this, R.string.enter_email, Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Sending password reset email to forgotten passwords
                    firebaseAuth = FirebaseAuth.getInstance();
                    progressBarForgotPassword.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(forgottenEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBarForgotPassword.setVisibility(View.GONE);
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, R.string.password_sent_in_email, Toast.LENGTH_SHORT).show();
                            }else{
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            }
                        });
                    }
                });


            }
        });

        //Check Shared Preference
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        if(checkbox.equals("true")){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
        }

        //Function of Login Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            email = et_email.getText().toString().trim();
            password = et_password.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                Toast.makeText(LoginActivity.this, R.string.enter_email, Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(LoginActivity.this, R.string.enter_password, Toast.LENGTH_LONG).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //SignIn
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(LoginActivity.this, R.string.login_successful,
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, R.string.login_failed,
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        //finish();
                    }
                    }
                });
            }
        });


        //Remember Me Button listener
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.isChecked()){
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "true");
                editor.apply();
            }else{
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
            }
            }
        });

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Facebook Login
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    //Toast.makeText(LoginActivity.this, R.string.login_successful_from_facebook, Toast.LENGTH_SHORT).show();
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, R.string.login_canceled_from_facebook, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    if (error instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                    }
                    Toast.makeText(LoginActivity.this, R.string.error_logging_in_facebook+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            }
        });


        //Google Sign In
        signInButton = findViewById(R.id.google_login);
        //firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }


    /*
        =======================
        Facebook SIGN IN
        =======================
     */
    private void handleFacebookAccessToken(AccessToken token) {
        //Toast.makeText(this, "Facebook Token is "+token, Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Toast.makeText(LoginActivity.this, R.string.login_successful_from_facebook, Toast.LENGTH_SHORT).show();
                        user = firebaseAuth.getCurrentUser();
                        uploadUserToFirestore(user);
                    }else{
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            });
    }

    private void uploadUserToFirestore(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        if(user != null) {
            String user_id = user.getUid();
            String photoUrl = user.getPhotoUrl().toString();
            String user_img = photoUrl + "?type=large";
            String user_name = user.getDisplayName();
            String user_address = null;
            String phone = user.getPhoneNumber();
            String email = user.getEmail();

            //Setting up the User
            User user_details = new User(user.getUid(),user_img,user_name, user_address, phone, email);

            //Uploading User Object to FireStore
            DocumentReference documentReference = firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.set(user_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.login_successful_from_facebook, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.login_failed_from_facebook+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /*
        =======================
        GOOGLE SIGN IN
        =======================
     */

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(getApplicationContext(), R.string.login_successful_from_google, Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            Toast.makeText(this, R.string.login_failed_from_google_message + e.getMessage(), Toast.LENGTH_SHORT).show();
            //FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                        user = firebaseAuth.getCurrentUser();
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        if(account != null){
                            String personName = account.getDisplayName();
                            String personEmail = account.getEmail();
                            String personalID = account.getId();
                            Uri personPhoto = account.getPhotoUrl();
                            String photoUrl  = personPhoto.toString();

                            User user_to_upload_from_google_data = new User(personalID, photoUrl, personName, null, null, personEmail);

                            //Uploading User Object to FireStore
                            DocumentReference documentReference = firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                            documentReference.set(user_to_upload_from_google_data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), R.string.login_successful_from_google, Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), R.string.login_failed_from_google_message+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, R.string.login_failed_from_google, Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.changelanguage:
                showChangeLanaugeDialog();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChangeLanaugeDialog() {
        //array of languages to display in alert dialog
        final String[] listItems = {"नेपाली","हिन्दी","English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //Nepali
                    setLocale("ne");
                    recreate();
                }
                else if(i == 1){
                    //Hindi
                    setLocale("hi");
                    recreate();
                }else if(i == 2){
                    //english
                    setLocale("en");
                    recreate();
                }
                //dismiss alert dialog when language selected
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        //show alert dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //Save data to shared Preference
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //Load lanaguages saved in shared preference
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() != null){
            //firebaseAuth.addAuthStateListener(authStateListener);
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

    }
}
