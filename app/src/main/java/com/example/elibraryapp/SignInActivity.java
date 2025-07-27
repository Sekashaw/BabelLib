package com.example.elibraryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class SignInActivity extends AppCompatActivity {
    // DATABASE
    DBHelper DB;

    // BUTTONS
    MaterialButton btnSignIn, btnSignUp;

    // INPUT FIELDS
    EditText etEmail, etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //DATABASE
        DB = new DBHelper(this);

        //BUTTONS
        btnSignIn = findViewById(R.id.signIn);
        btnSignUp = findViewById(R.id.signUp);

        //INPUT FIELDS
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        //BACK BUTTON HANDLER
        //When the user logs out this is to ensure that when you press back in the sign in page you go to the Signup page
        //instead of where ever the user logged out from
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                goToActivity(MainActivity.class, "");
            }
        });

        // BUTTON : ON CLICK LISTENERS
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(MainActivity.class);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTXT = etEmail.getText().toString();
                String passwordTXT = etPassword.getText().toString();

                if (emailTXT.isEmpty()){
                    Toast.makeText(SignInActivity.this, "Enter an Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordTXT.isEmpty())
                {
                    Toast.makeText(SignInActivity.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(DB.userExists(emailTXT))
                {
                    if(DB.validatePassword(emailTXT,passwordTXT)) {
                        Toast.makeText(SignInActivity.this, "Logging In", Toast.LENGTH_SHORT).show();
                        goToActivity(Store.class, emailTXT);
                    }
                    else
                        Toast.makeText(SignInActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SignInActivity.this, "User Does not Exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // FUNCTIONS

    //Goes to a given activity
    private void goToActivity(Class<?> activity)
    {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    //Goes to a given activity and passes information on which account is currently logged in
    private void goToActivity(Class<?> activity, String account)
    {
        Intent intent = new Intent(this, activity);
        intent.putExtra("Account", account);
        startActivity(intent);
    }
}