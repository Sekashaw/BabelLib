package com.example.elibraryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Store extends AppCompatActivity {
    //ACCOUNT INFO
    String account;

    //DATABASE
    DBHelper DB;

    //LAYOUTS
    LinearLayout mainLayout;

    //BUTTONS
    MaterialButton btnLibrary, btnCart, btnLogout;

    //COLORS
    int baseColor,btnColor,btnTextColor,errorColor;
    int[] colors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //ACCOUNT INFO
        account = getIntent().getStringExtra("Account");

        //DATABASE
        DB = new DBHelper(this);

        //LAYOUTS
        mainLayout = findViewById(R.id.dataLayout);

        //BUTTONS
        btnCart = findViewById(R.id.cart);
        btnLibrary = findViewById(R.id.library);
        btnLogout = findViewById(R.id.logout);

        //COLORS
        baseColor = ContextCompat.getColor(this, R.color.text_color);
        btnColor = ContextCompat.getColor(this, R.color.button_color);
        btnTextColor = ContextCompat.getColor(this, R.color.button_text_color);
        errorColor = ContextCompat.getColor(this, R.color.error_text_color);
        colors = new int[]{baseColor, btnColor, btnTextColor, errorColor};

        //ADDING PRODUCTS TO THE STORE LAYOUT
        DB.addProductViews(account,mainLayout,DB.getData("Product"),this,colors);

        // BUTTON : ON CLICK LISTENERS
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(CartActivity.class,account);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(SignInActivity.class, "");
            }
        });

        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(LibraryActivity.class, account);
            }
        });

        //BACK BUTTON HANDLER
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(Store.this);
                alert.setTitle("Log out");
                alert.setMessage("Are you sure you want to Log out?");

                // If user taps Yes then logout
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToActivity(SignInActivity.class, "");
                    }
                });

                // If user taps No then Close the alert
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    // FUNCTIONS

    //Goes to a given activity and passes information on which account is currently logged in
    private void goToActivity(Class<?> activity, String account)
    {
        Intent intent = new Intent(this, activity);
        intent.putExtra("Account", account);
        startActivity(intent);
    }
}