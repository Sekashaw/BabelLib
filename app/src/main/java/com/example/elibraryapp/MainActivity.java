package com.example.elibraryapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

// Sign Up Activity Class
public class MainActivity extends AppCompatActivity {

    // DATABASE
    DBHelper DB;

    // BUTTONS
    MaterialButton btnSignIn, btnSignUp;

    // INPUT FIELDS
    EditText etEmail, etPassword, etPasswordRepeat;

    //COLORS
    int errorColor, baseColor;

    // OTHER
    // PASSWORD REQUIREMENTS
    TextView passReq1,passReq2,passReq3,passReq4,passReq5,infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // DATABASE
        DB = new DBHelper(this);

        // BUTTONS
        btnSignIn = findViewById(R.id.signIn);
        btnSignUp = findViewById(R.id.signUp);

        // INPUT FIELDS
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etPasswordRepeat = findViewById(R.id.passwordRepeat);

        //COLORS
        errorColor = ContextCompat.getColor(this, R.color.error_text_color);
        baseColor = ContextCompat.getColor(this, R.color.text_color);

        // PASSWORD REQUIREMENTS
        passReq1 = findViewById(R.id.passReq1);
        passReq2 = findViewById(R.id.passReq2);
        passReq3 = findViewById(R.id.passReq3);
        passReq4 = findViewById(R.id.passReq4);
        passReq5 = findViewById(R.id.passReq5);
        infoText = findViewById(R.id.infoText);


        // BUTTON : ON CLICK LISTENERS
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(SignInActivity.class);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTXT = etEmail.getText().toString();
                String passwordTXT = etPassword.getText().toString();
                String passwordRepeatTXT = etPasswordRepeat.getText().toString();

                if(emailTXT.isEmpty() || !DB.validateEmail(emailTXT))
                {
                    Toast.makeText(MainActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (passwordTXT.isEmpty())
                    {
                        Toast.makeText(MainActivity.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(DB.userExists(emailTXT))
                    {
                        Toast.makeText(MainActivity.this, "This user already exists", Toast.LENGTH_SHORT).show();
                    }
                    else if (!passwordRepeatTXT.equals(passwordTXT))
                    {
                        Toast.makeText(MainActivity.this, "Both passwords must match. Try again", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        boolean[] prompts = DB.setPasswordPrompts(passwordTXT);
                        setPrompts(prompts,errorColor,baseColor);
                        if(DB.checkPasswordPattern(prompts))
                        {
                            Boolean checkInsertData = DB.insertUserData(emailTXT, passwordTXT);
                            if(checkInsertData) {
                                infoText.setText("Account Created");
                                Toast.makeText(MainActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                goToActivity(Store.class, emailTXT);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Account not Created", Toast.LENGTH_SHORT).show();
                                infoText.setText("Account Not Created");
                            }
                        }
                        else
                        {
                            infoText.setText("Password must have");
                            Toast.makeText(MainActivity.this, "Password does not meet requirements", Toast.LENGTH_LONG).show();
                        }
                    }
                }
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

    //take the array generated by the password and hide/show password requirements
    //based on the password
    private void setPrompts(boolean[] prompts, int errorColor, int baseColor)
    {
        if(prompts[0]) {
            passReq1.setVisibility(GONE);
            passReq1.setTextColor(baseColor);
        }
        else
        {
            passReq1.setVisibility(VISIBLE);
            passReq1.setTextColor(errorColor);
        }

        if(prompts[1]) {
            passReq2.setVisibility(GONE);
            passReq2.setTextColor(baseColor);
        }
        else
        {
            passReq2.setVisibility(VISIBLE);
            passReq2.setTextColor(errorColor);
        }

        if(prompts[2]) {
            passReq3.setVisibility(GONE);
            passReq3.setTextColor(baseColor);
        }
        else
        {
            passReq3.setVisibility(VISIBLE);
            passReq3.setTextColor(errorColor);
        }

        if(prompts[3]) {
            passReq4.setVisibility(GONE);
            passReq4.setTextColor(baseColor);
        }
        else
        {
            passReq4.setVisibility(VISIBLE);
            passReq4.setTextColor(errorColor);
        }

        if(prompts[4]) {
            passReq5.setVisibility(GONE);
            passReq5.setTextColor(baseColor);
        }
        else
        {
            passReq5.setVisibility(VISIBLE);
            passReq5.setTextColor(errorColor);
        }
    }
}