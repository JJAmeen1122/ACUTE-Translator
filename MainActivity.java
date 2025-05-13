package com.jjapp.acuetranslator;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton Btndrawer;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to TextView
        TextView welcomeText = findViewById(R.id.welcomeText);

        // Create styled text
        SpannableString spannableString = new SpannableString("Welcome to ACUTE Translator");
        spannableString.setSpan(
                new StyleSpan(Typeface.BOLD),
                10, // start index of "JJA Translator"
                spannableString.length(), // end index
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set the styled text
        welcomeText.setText(spannableString);

        // Add animation
        welcomeText.setAlpha(0f);
        welcomeText.setScaleX(0.7f);
        welcomeText.setScaleY(0.7f);
        welcomeText.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .start();


        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in, if not redirect to login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        // Initialize views
        drawerLayout = findViewById(R.id.drawerlayout);
        Btndrawer = findViewById(R.id.Btndrawer);
        navigationView = findViewById(R.id.nav_view);

        // Set up translator button
        Button translatorButton = findViewById(R.id.tranBtn);
        translatorButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, lantrans.class));
        });

        // Set up drawer button
        Btndrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });

        // Update navigation header with user data
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.userNameTextView);
        TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);

        userNameTextView.setText(currentUser.getDisplayName() != null ?
                currentUser.getDisplayName() : "User");
        userEmailTextView.setText(currentUser.getEmail());

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.translator) {
                startActivity(new Intent(MainActivity.this, lantrans.class));
            } else if (id == R.id.about) {
                Intent aboutIntent = new Intent(MainActivity.this, about.class);
                startActivity(aboutIntent);
            }else if (id == R.id.logoutButton) {
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, login.class));
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }
}