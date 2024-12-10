package com.example.musicmanagement;

import static com.example.musicmanagement.Utils.isValidEmail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Signup extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        // ===============================================
        // connectdb
        databaseHelper = new DatabaseHelper(getApplicationContext());
        // redirect login page
        TextView redirectBtn = findViewById(R.id.txtRedirectLogin);
        redirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
        // signup
        EditText editTextEmail = findViewById(R.id.txtEmail);
        EditText editTextPassword = findViewById(R.id.txtPass);
        EditText editTextConfirmPassword = findViewById(R.id.txtConfirmPass);
        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(databaseHelper.checkUserExist(email)) {
                    Toast.makeText(getApplicationContext(), "This email is already registered.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password and confirm password do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseHelper.insertUser(email,password);
                Toast.makeText(getApplicationContext(), "Register successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }
}