package com.example.elibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class CartActivity extends AppCompatActivity {
    //ACCOUNT INFO
    String account;

    //DATABASE
    DBHelper DB;

    //LAYOUTS
    LinearLayout mainLayout, checkoutSection, itemSection;

    //BUTTONS
    MaterialButton btnStore, btnLibrary, btnCheckout, btnLogout,btnEmptyCart;

    //TEXT VIEWS
    TextView emptyText,totalText;

    //COLORS
    int baseColor,btnColor,btnTextColor,errorColor;
    int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
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
        checkoutSection = findViewById(R.id.checkoutSection);
        itemSection = findViewById(R.id.itemSection);

        //BUTTONS
        btnStore = findViewById(R.id.store);
        btnLibrary = findViewById(R.id.library);
        btnCheckout = findViewById(R.id.checkout);
        btnLogout = findViewById(R.id.logout);
        btnEmptyCart = findViewById(R.id.emptyCart);

        //TEXT VIEWS
        emptyText = findViewById(R.id.emptyText);
        totalText = findViewById(R.id.total);

        //COLORS
        baseColor = ContextCompat.getColor(this, R.color.text_color);
        btnColor = ContextCompat.getColor(this, R.color.button_color);
        btnTextColor = ContextCompat.getColor(this, R.color.button_text_color);
        errorColor = ContextCompat.getColor(this, R.color.error_text_color);
        colors = new int[]{baseColor, btnColor, btnTextColor, errorColor};

        //Adding Cart Items from the Database to the Layout
        int total = DB.addCartViews(mainLayout,DB.getCartData(account),this,colors,emptyText,itemSection,checkoutSection);
        totalText.setText("Total = $ " + total);

        // BUTTON : ON CLICK LISTENERS
        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(Store.class, account);
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DB.userHasNoProducts(account)){
                    Toast.makeText(CartActivity.this, "Already Checked Out", Toast.LENGTH_SHORT);
                    goToActivity(CartActivity.class, account);
                }
                else
                {
                    //move the products to the library
                    DB.checkOut(account);

                    Toast.makeText(CartActivity.this, "Checked Out", Toast.LENGTH_LONG);

                    //this is essential so that the user cannot go back to the cart and checkout multiple times
                    //this removes the activity from the "back" stack so it cannot be gone back to
                    finish();
                    goToActivity(CartActivity.class, account);
                }
            }
        });

        btnEmptyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove only cart items from the database (Library data stays)
                DB.emptyCart(account);

                finish();
                goToActivity(CartActivity.class, account);
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