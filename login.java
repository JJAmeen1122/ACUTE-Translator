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

public class login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(login.this, MainActivity.class));
            finish();
            return;
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.Btnlogin);
        signUpTextView = findViewById(R.id.signUpTextView);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(v -> loginUser());
        signUpTextView.setOnClickListener(v -> {
            startActivity(new Intent(login.this, signup.class));
            finish();
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Login success
                        Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this, MainActivity.class));
                        finish();
                    } else {
                        // If login fails, check if user needs to sign up
                        if (task.getException() != null &&
                                task.getException().getMessage() != null &&
                                task.getException().getMessage().contains("no user record")) {

                            Intent intent = new Intent(login.this, signup.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(login.this, "Login failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}