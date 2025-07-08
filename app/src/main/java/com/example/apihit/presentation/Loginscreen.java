package com.example.apihit.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apihit.R;
import com.example.apihit.data.StudentRepository;
import com.example.apihit.preferences.SharedPrefs;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;


public class Loginscreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private StudentRepository studentRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(com.example.apihit.R.layout.activity_loginscreen);

        SharedPrefs sharedPrefs = new SharedPrefs();
        String token = sharedPrefs.getToken(getApplicationContext());
        String username = sharedPrefs.getUsername(getApplicationContext());

        Log.d("SharedPreferences",""+sharedPrefs.getToken(getApplicationContext())+sharedPrefs.getUsername(getApplicationContext()));

        if(token != null && !token.isEmpty() && username != null && !username.isEmpty()){
            Intent it = new Intent(Loginscreen.this, NewsChannel.class);
            startActivity(it);
            finish();
        }


        mAuth = FirebaseAuth.getInstance();
        studentRepository = new StudentRepository(getApplicationContext());

        Button btnLogin = findViewById(com.example.apihit.R.id.myMaterialButton);
        EditText etUsername = findViewById(com.example.apihit.R.id.etUsername);
        EditText etPassword = findViewById(com.example.apihit.R.id.etPassword);

        btnLogin.setOnClickListener(v -> {
            String email = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Loginscreen.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                Log.e("Login", "Fields empty");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Loginscreen.this, "Firebase Login Successful", Toast.LENGTH_SHORT).show();
                            Log.i("Login", "Firebase login successful for: " + email);
                            // Call backend login after Firebase login
                            String tempEmail = "somnathnalawade257@gmail.com";
                            String tempPass = "somnath@204";
                            studentRepository.login(tempEmail, tempPass, new StudentRepository.LoginCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.i("Login", "Backend login successful, JWT stored");
                                    SharedPrefs.saveUsername(getApplicationContext(),email);
                                    Intent it = new Intent(Loginscreen.this, MainActivity.class);
                                    startActivity(it);
                                    finish();
                                    Log.d("SharedPreferences",""+SharedPrefs.getUsername(getApplicationContext()));
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("Login", "Backend login failed: " + e.getMessage());
                                    Toast.makeText(Loginscreen.this, "Backend login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Log.e("Login", "Firebase login failed: " + task.getException());
                            Toast.makeText(Loginscreen.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        TextView tvRegister = findViewById(com.example.apihit.R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(Loginscreen.this, RegisterActivity.class));
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.apihit.R.string.default_web_client_id)) // ✅ Make sure this string exists
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

// Set button click
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(v -> signIn());

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user != null ? user.getEmail() : null;
                        Toast.makeText(this, "Firebase Google login successful: " + email, Toast.LENGTH_SHORT).show();
                        Log.i("Login", "Firebase Google login successful for: " + email);
                        // Call backend login after Google login (password not available, use a placeholder or handle accordingly)
                        studentRepository.login(email, "google-oauth", new StudentRepository.LoginCallback() {
                            @Override
                            public void onSuccess() {
                                Log.i("Login", "Backend login successful, JWT stored (Google)");
                                Intent intent = new Intent(Loginscreen.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                Log.e("Login", "Backend login failed (Google): " + e.getMessage());
                                Toast.makeText(Loginscreen.this, "Backend login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.e("Login", "Firebase Google login failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        Toast.makeText(this, "Firebase login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}