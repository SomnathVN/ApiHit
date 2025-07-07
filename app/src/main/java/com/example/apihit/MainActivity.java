package com.example.apihit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextAge;
    private Button buttonNext;
    private TextView textViewError;
    private StudentRepository studentRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // Initialize the new student repository
        studentRepository = new StudentRepository();

        // Set click listener for Next button
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
            }
        });
    }

    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAge = findViewById(R.id.editTextAge);
        buttonNext = findViewById(R.id.buttonNext);
        textViewError = findViewById(R.id.textViewError);
    }

    private void validateAndProceed() {
        // Get input values
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();

        // Hide error message initially
        textViewError.setVisibility(View.GONE);

        // Validate inputs
        if (name.isEmpty()) {
            showError("Please enter your name");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError("Please enter your email address");
            editTextEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        if (ageStr.isEmpty()) {
            showError("Please enter your age");
            editTextAge.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                showError("Please enter a valid age (1-120)");
                editTextAge.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid age");
            editTextAge.requestFocus();
            return;
        }

        // Call the new student API and Firestore
        Student student = new Student(name, email, age);
        studentRepository.addStudent(student, new StudentRepository.StudentAddCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Student added to Firestore!", Toast.LENGTH_SHORT).show());
                // Optionally, proceed to next activity or clear fields
                Log.d("API-RESPONSE","The student added to firestore");
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("API-RESPONSE", "The student not added to firestore", e);
                if (e != null) {
                    Log.e("API-RESPONSE", "Exception class: " + e.getClass().getName());
                    Log.e("API-RESPONSE", "Exception toString: " + e.toString());
                    Log.e("API-RESPONSE", "Exception message: " + e.getMessage());
                } else {
                    Log.e("API-RESPONSE", "Exception is null!");
                }
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show());
            }
        });

        // If all validations pass, proceed to next activity (existing logic)
        proceedToNextActivity(name, email, age);
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showError(String message) {
        textViewError.setText(message);
        textViewError.setVisibility(View.VISIBLE);
    }

    private void proceedToNextActivity(String name, String email, int age) {
        // Create intent to pass data to next activity
        Intent intent = new Intent(MainActivity.this, NewsChannel.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("age", age);

        // Show success message
        Toast.makeText(this, "Information saved successfully!", Toast.LENGTH_SHORT).show();

        // Start next activity
        startActivity(intent);
    }

    // Optional: Clear all fields
    private void clearFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextAge.setText("");
        textViewError.setVisibility(View.GONE);
    }

    // Optional: Handle back button press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
