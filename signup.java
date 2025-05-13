package com.jjapp.acuetranslator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class signup extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private ProgressBar progressBar;
    TextView loginTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.BtnsignUp);
        progressBar = findViewById(R.id.progressBar);
        loginTextView= findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(signup.this, login.class));
            finish();
        });
        // Pre-fill email if coming from login
        String email = getIntent().getStringExtra("email");
        if (email != null) {
            emailEditText.setText(email);
        }
        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Update user profile with name
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(profileTask -> {
                                    if (profileTask.isSuccessful()) {
                                        // Registration success, go to MainActivity
                                        Toast.makeText(signup.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(signup.this, MainActivity.class));
                                        finish();
                                    }
                                });
                    } else {
                        Toast.makeText(signup.this, "Registration failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}