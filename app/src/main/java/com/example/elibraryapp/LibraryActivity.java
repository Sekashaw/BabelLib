package com.example.elibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class LibraryActivity extends AppCompatActivity {
    //ACCOUNT INFO
    String account;

    //DATABASE
    DBHelper DB;

    //LAYOUTS
    LinearLayout mainLayout, itemSection;

    //BUTTONS
    MaterialButton btnStore, btnCart, btnLogout;

    //TEXT VIEWS
    TextView emptyText;

    //COLORS
    int baseColor,btnColor,btnTextColor,errorColor;
    int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library);
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
        mainLayout = findViewById(R.id.itemList);

        itemSection = findViewById(R.id.itemSection);

        //BUTTONS
        btnStore = findViewById(R.id.store);
        btnCart = findViewById(R.id.cart);
        btnLogout = findViewById(R.id.logout);

        //TEXT VIEWS
        emptyText = findViewById(R.id.emptyText);

        //COLORS
        baseColor = ContextCompat.getColor(this, R.color.text_color);
        btnColor = ContextCompat.getColor(this, R.color.button_color);
        btnTextColor = ContextCompat.getColor(this, R.color.button_text_color);
        errorColor = ContextCompat.getColor(this, R.color.error_text_color);
        colors = new int[]{baseColor, btnColor, btnTextColor, errorColor};

        //Adding Library Items from the Database to the Layout
        DB.addLibraryViews(mainLayout,DB.getLibraryData(account),this,colors,emptyText,itemSection);

        // BUTTON : ON CLICK LISTENERS
        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(Store.class, account);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(SignInActivity.class, "");
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(CartActivity.class, account);
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