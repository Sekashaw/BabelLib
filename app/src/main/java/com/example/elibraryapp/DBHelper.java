package com.example.elibraryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import java.util.Arrays;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        // User Table stores login credentials
        // Product Table stores product information
        // UserProduct Table stores which products a user owns and which they have just added to their cart
        DB.execSQL("create Table IF NOT EXISTS Userdetails(email TEXT primary key, password TEXT)");
        DB.execSQL("CREATE TABLE IF NOT EXISTS Product(productID INTEGER, productName TEXT, productPrice INTEGER, productDescription TEXT, PRIMARY KEY(productID));");
        DB.execSQL("CREATE TABLE IF NOT EXISTS UserProduct(email TEXT REFERENCES Userdetails(email), productID INTEGER REFERENCES Product(productID), amount INTEGER, inLibrary INTEGER DEFAULT 0, PRIMARY KEY(email,productID,inLibrary));");

        //PRODUCTS
        DB.execSQL("INSERT INTO Product VALUES(1, \"How to do backflips\", 1000, \"Backflips don't come cheap\");");
        DB.execSQL("INSERT INTO Product VALUES(2, \"The Light Light Novel\", 20, \"A light novel about light\");");
        DB.execSQL("INSERT INTO Product VALUES(3, \"Cookbook for dummies\", 80, \"A cookbook for dumb people\");");
        DB.execSQL("INSERT INTO Product VALUES(4, \"History of History books\", 10, \"A book about history\");");
        DB.execSQL("INSERT INTO Product VALUES(5, \"How to lose all your money\", 5000, \"You should buy this\");");
        DB.execSQL("INSERT INTO Product VALUES(6, \"Soccer Playbook\", 700, \"Win every match\");");
        DB.execSQL("INSERT INTO Product VALUES(7, \"Android Studio Manual\", 5, \"Learn this janky app\");");
        DB.execSQL("INSERT INTO Product VALUES(8, \"I love Placeholder text\", 100, \"Not a placeholder\");");
        DB.execSQL("INSERT INTO Product VALUES(9, \"How to Draw Anime\", 500, \"Become an Anime Genius\");");
        DB.execSQL("INSERT INTO Product VALUES(10, \"Tourist Locations in Canada\", 1000, \"Where do we go?\");");
        DB.execSQL("INSERT INTO Product VALUES(11, \"How to write a Story\", 200, \"plot armour free\");");
        DB.execSQL("INSERT INTO Product VALUES(12, \"Find all the buttons\", 50, \"A picture book\");");
        DB.execSQL("INSERT INTO Product VALUES(13, \"How to play chess\", 100, \"en Passant\");");
        DB.execSQL("INSERT INTO Product VALUES(14, \"Hi\", 10, \"Hello\");");
        DB.execSQL("INSERT INTO Product VALUES(15, \"How to Reward your Players\", 0, \"Wow you scrolled this far!\");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL("drop Table if exists Userdetails");
        DB.execSQL("drop Table if exists Product");
        DB.execSQL("drop Table if exists UserProduct");
        DB.execSQL("drop Table if exists Test");
        onCreate(DB);
    }

    public boolean insertUserData(String email, String password)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("email", email);
        contentValues.put("password",password);

        long result = DB.insert("Userdetails", null, contentValues);

        // return result != -1
        return result != -1;
    }
    public void insertProduct(int productID, String productName, int productPrice, String productDescription)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("productID", productID);
        contentValues.put("productName",productName);
        contentValues.put("productPrice", productPrice);
        contentValues.put("productDescription",productDescription);


        DB.insert("Product", null, contentValues);
    }

    //Called when user Clicks add to cart button on a product
    //If the user has the product in their cart, update the amount of that product in their cart
    //If the user doesn't have the product in their cart (first time adding to cart) then add it to the cart
    public void addToCart(String email, int productID, int amount)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(userHasProductInCart(email,productID))
        {
            DB.execSQL("UPDATE UserProduct SET amount = amount + ? WHERE email = ? AND productID = ? AND inLibrary=0",new String[]{Integer.toString(amount),email,Integer.toString(productID)});
        }
        else
        {
            contentValues.put("email", email);
            contentValues.put("productID",productID);
            contentValues.put("amount", amount);
            DB.insert("UserProduct", null, contentValues);
        }
    }

    //Called when user clicks the checkout button in the Cart
    //If the user has the product in their library, update the amount of that product in their library
    //If the user doesn't have the product in their library (first time buying the product) then add it to their library
    public void checkOut(String email)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor res = getCartData(email);

        while (res.moveToNext())
        {
            int productID = Integer.parseInt(res.getString(1));
            int amount = Integer.parseInt(res.getString(2));
            if(userHasProductInLibrary(email,productID))
            {
                //Add cart amounts to library amounts
                DB.execSQL("UPDATE UserProduct SET amount = amount + ? WHERE email = ? AND productID = ? AND inLibrary=1",new String[]{Integer.toString(amount),email,Integer.toString(productID)});

                //remove old cart entries since they are already checked out
                DB.execSQL("DELETE FROM UserProduct WHERE email = ? AND productID = ? AND inLibrary=0",new String[]{email,Integer.toString(productID)});
            }
            else
            {
                //if the product wasn't in the library already, put it in the library
                DB.execSQL("UPDATE UserProduct SET inLibrary=1 WHERE email = ? AND productID = ? AND inLibrary=0",new String[]{email,Integer.toString(productID)});
            }
        }
    }

    public boolean userExists(String email)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Userdetails where email = ?", new String[]{email});
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean userHasProductInCart(String email, int productID)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserProduct where email = ? and productID=? and inLibrary=0", new String[]{email,Integer.toString(productID)});
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean userHasProductInLibrary(String email, int productID)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserProduct where email = ? and productID=? and inLibrary=1", new String[]{email,Integer.toString(productID)});

        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean userHasNoProducts(String email)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserProduct where email = ?", new String[]{email});

        if(cursor.getCount() > 0)
            return false;
        else
            return true;
    }

    public Cursor getData(String table)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from " + table, null);
        return cursor;
    }

    //Get data from UserProduct table that is specifically marked as a cart product
    public Cursor getCartData(String email)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from UserProduct WHERE email = ? and inLibrary = 0",new String[]{email});
        return cursor;
    }

    //Get data from UserProduct table that is specifically marked as a library product
    public Cursor getLibraryData(String email)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from UserProduct WHERE email = ? and inLibrary = 1",new String[]{email});
        return cursor;
    }

    //This only empties the cart of the specified user
    public boolean emptyCart(String email)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserProduct where email = ? and inLibrary = 0", new String[]{email});

        if(cursor.getCount() > 0) {
            long result = DB.delete("UserProduct", "email=? and inLibrary = 0", new String[]{email});

            // return result != -1
            if (result == -1)
                return false;
            else
                return true;
        }
        else
            return false;
    }

    public Cursor getProductDetails(int productID)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Product WHERE productID = ?",new String[]{Integer.toString(productID)});
        return cursor;
    }

    //not to be confused with the setPasswordPrompts() function
    //this function only checks if the password given is the same as the password in the database.
    //called in the sign in page
    public boolean validatePassword(String email, String password)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails where email = ? and password = ?", new String[]{email,password});

        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    // returns an array that is used to set which password hints are shown to the user
    // based on which password requirement is missing
    // called in the sign up page when creating an account
    public boolean[] setPasswordPrompts(String password) {
        boolean[] valid = new boolean[5];

        Arrays.fill(valid, true);

        System.out.println(valid[0]);

        // at least 8 characters
        if (password.length() < 8) {
            valid[0] = false;
        }

        // at least 1 uppercase
        if (!password.matches(".*[A-Z].*")) {
            valid[1] = false;
        }

        // at least 1 lowercase
        if (!password.matches(".*[a-z].*")) {
            valid[2] = false;
        }

        //at least 1 digit
        if (!password.matches(".*\\d.*")) {
            valid[3] = false;
        }

        //at least 1 special character
        if (!password.matches(".*[_?!@#$%^&*()\\-+].*")) {
            valid[4] = false;
        }

        return valid;
    }

    //take the array from above, if any of the values are false
    //then return false, which means that the password is invalid
    public boolean checkPasswordPattern(boolean[] prompts)
    {
        for (boolean prompt: prompts) {
            if(!prompt)
                return false;
        }
        return true;
    }

    //email must be of the format text@text
    public boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    // Populates a linear layout (in this case the dataLayout in the Store Page) with Products from the database
    public void addProductViews(String account, LinearLayout mainlayout, Cursor res, Context context, int[] colors) {
        if (res.getCount() == 0)
            return;

        while (res.moveToNext())
        {
            StringBuilder row = new StringBuilder();
            LinearLayout subLayout = new LinearLayout(context);

            /*LAYOUT
            * Name of the Book
            * $Price
            * Description of the Book
            * NOTE: column index 0 contains productID, this is not shown to the user
            * */
            int prodID = Integer.parseInt(res.getString(0));
            row.append( res.getString(1) +
                        "\n" + res.getString(3) +
                        "\n$" + res.getString(2));

            TextView tv = new TextView(context);
            EditText et = new EditText(context);
            MaterialButton tvBtn = new MaterialButton(context);

            //SHARED LAYOUT SETTINGS
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

            //TEXT VIEW SETTINGS

            // setting layout_width and layout_height to match_parent and layout_weight to 1
//            params.weight=1f;
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f));

            //add data a single row to the text view
            tv.setText(row.toString());
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTextColor(colors[0]);

            //EDIT TEXT SETTINGS
            params.weight=2f;
            et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,2f));
            et.setHint("amount");
            et.setInputType(InputType.TYPE_CLASS_NUMBER);


            //MATERIAL BUTTON SETTINGS
            tvBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,2f));
            tvBtn.setText("Add to Cart");
            tvBtn.setTextColor(colors[2]);
            tvBtn.setBackgroundTintList(ColorStateList.valueOf(colors[1]));
            tvBtn.setCornerRadius(0);

            tvBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!et.getText().toString().isEmpty())
                    {
                        int amount = Integer.parseInt(et.getText().toString());
                        System.out.println(account);

                        if (amount > 0)
                        {
                            addToCart(account,prodID,amount);
                            Toast.makeText(context,"Added to Cart",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(context,"No amount specified",Toast.LENGTH_SHORT).show();
                }
            });

            subLayout.addView(tv);
            subLayout.addView(et);
            subLayout.addView(tvBtn);
            mainlayout.addView(subLayout);
        }
    }

    // Populates a linear layout (in this case the dataLayout in the Cart Page) with Products from the database
    public int addCartViews(LinearLayout mainlayout, Cursor res, Context context, int[] colors, TextView emptyText, LinearLayout itemSection, LinearLayout checkoutSection)
    {
        if (res.getCount() == 0)
        {
            emptyText.setVisibility(View.VISIBLE);
            itemSection.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.GONE);
            return 0;
        }
        else
        {
            emptyText.setVisibility(View.GONE);
            itemSection.setVisibility(View.VISIBLE);
            checkoutSection.setVisibility(View.VISIBLE);
        }
        int total = 0;
        while (res.moveToNext())
        {
            LinearLayout subLayout = new LinearLayout(context);

            //GETTING DATA FROM PRODUCT TABLE
            //this gives us id,name,price and description (only name and price are needed)
            Cursor products = getProductDetails(Integer.parseInt(res.getString(1)));
            String name = "";
            String price = "";
            while (products.moveToNext())
            {
                name = products.getString(1);
                price = products.getString(2);
            }

            TextView tvBook = new TextView(context);
            TextView tvPrice = new TextView(context);
            TextView tvAmount = new TextView(context);
            TextView tvTotal = new TextView(context);

            //SHARED LAYOUT SETTINGS
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
            tvBook.setLayoutParams(params);
            tvPrice.setLayoutParams(params);
            tvAmount.setLayoutParams(params);
            tvTotal.setLayoutParams(params);

            //BOOK SETTINGS

            //add data a single row to the text view
            tvBook.setText(name);
            tvBook.setBackgroundColor(colors[2]);
            tvBook.setTextColor(colors[1]);
            tvBook.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvBook.setPadding(10,10,10,10);

            //PRICE SETTINGS
            tvPrice.setText(price);
            tvPrice.setBackgroundColor(colors[2]);
            tvPrice.setTextColor(colors[1]);
            tvPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvPrice.setPadding(10,10,10,10);


            //AMOUNT SETTINGS
            String amount = res.getString(2);
            tvAmount.setText(amount);
            tvAmount.setBackgroundColor(colors[2]);
            tvAmount.setTextColor(colors[1]);
            tvAmount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvAmount.setPadding(10,10,10,10);

            //TOTAL = PRICE * AMOUNT
            int singleTotal = Integer.parseInt(price) * Integer.parseInt(amount);
            total += singleTotal;

            //TOTAL SETTINGS
            tvTotal.setText(Integer.toString(singleTotal));
            tvTotal.setBackgroundColor(colors[2]);
            tvTotal.setTextColor(colors[1]);
            tvTotal.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvTotal.setPadding(10,10,10,10);

            subLayout.addView(tvBook);
            subLayout.addView(tvPrice);
            subLayout.addView(tvAmount);
            subLayout.addView(tvTotal);

            mainlayout.addView(subLayout);
        }
        return total;
    }

    // Populates a linear layout (in this case the dataLayout in the Library Page) with Products from the database
    public void addLibraryViews(LinearLayout mainLayout, Cursor res, Context context, int[] colors, TextView emptyText, LinearLayout itemSection)
    {
        if (res.getCount() == 0)
        {
            emptyText.setVisibility(View.VISIBLE);
            itemSection.setVisibility(View.GONE);
        }
        else
        {
            emptyText.setVisibility(View.GONE);
            itemSection.setVisibility(View.VISIBLE);
        }

        while (res.moveToNext())
        {
            LinearLayout subLayout = new LinearLayout(context);

            //GETTING DATA FROM PRODUCT TABLE
            //this gives us id,name,price and description (only name is needed)
            Cursor products = getProductDetails(Integer.parseInt(res.getString(1)));
            String name = "";
            while (products.moveToNext())
            {
                name = products.getString(1);
            }

            TextView tvBook = new TextView(context);
            TextView tvAmount = new TextView(context);


            //SHARED LAYOUT SETTINGS
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
            tvBook.setLayoutParams(params);
            tvAmount.setLayoutParams(params);

            //BOOK SETTINGS

            //add data a single row to the text view
            tvBook.setText(name);
            tvBook.setBackgroundColor(colors[2]);
            tvBook.setTextColor(colors[1]);
            tvBook.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvBook.setPadding(10,10,10,10);


            //AMOUNT SETTINGS
            String amount = res.getString(2);
            tvAmount.setText(amount);
            tvAmount.setBackgroundColor(colors[2]);
            tvAmount.setTextColor(colors[1]);
            tvAmount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvAmount.setPadding(10,10,10,10);

            subLayout.addView(tvBook);
            subLayout.addView(tvAmount);

            mainLayout.addView(subLayout);
        }
    }
}
