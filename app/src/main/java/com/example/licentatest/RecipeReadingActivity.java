package com.example.licentatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class RecipeReadingActivity extends AppCompatActivity {
    private static final String TAG = "DescriptionReadingActiv";
    private static final String BASKET_FILE_NAME = "Basket.txt";
    private static final String INV_FILE_NAME = "INVENTORY.txt";
    private static final String MISS_INV_FILE_NAME = "MISSINVENTORY.txt";

    TextView mReadingView;
    Button mBuyBtn;

    private ArrayList<ProductItem> mFullInventory;

    //Looping vars
    Handler mDataHandler = new Handler();
    Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_reading);

        mReadingView = findViewById(R.id.readingView);
        mBuyBtn = findViewById(R.id.buyButton);

        final Recipe recipe = getIntent().getParcelableExtra("recipe");

        mReadingView.setText(recipe.getDescription());

        mFullInventory = loadFromFile(INV_FILE_NAME);
        ArrayList<ProductItem> tmp = loadFromFile(MISS_INV_FILE_NAME);
        mFullInventory.addAll(tmp);

        mBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ProductItem> items = loadFromFile(BASKET_FILE_NAME);
                StringTokenizer stringTokenizer = new StringTokenizer(recipe.getProducts(), ",");

                while(stringTokenizer.hasMoreTokens()) {
                    String product = stringTokenizer.nextToken();

                    for(ProductItem item : mFullInventory) {
                        if(item.getName().toLowerCase().equals(product.toLowerCase())) {
                            items.add(item);
                            break;
                        }
                    }
                }

                saveBasketToFile(BASKET_FILE_NAME, items);

                startActivity(new Intent(RecipeReadingActivity.this, BasketActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        mDataHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {

                mDataHandler.postDelayed(mRunnable, MainActivity.mDataDelay);
            }
        }, 100);

        super.onResume();
    }

    private ArrayList<ProductItem> loadFromFile(String FILENAME) {
        ArrayList<ProductItem> data = new ArrayList<>();
        FileInputStream fis;
        File file = new File(MainActivity.FILE_PATH + FILENAME);

        if (file.exists()) {
            try {
                fis = openFileInput(FILENAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String text;

                while ((text = br.readLine()) != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(text, ",");

                    String name = stringTokenizer.nextToken();
                    String URL = stringTokenizer.nextToken();
                    String cod = stringTokenizer.nextToken();
                    String quantity = stringTokenizer.nextToken();
                    String priceString = stringTokenizer.nextToken();
                    double price = 0;
                    if (priceString.matches(Globals.getInstance().fpRegex))
                        price = Double.parseDouble(priceString);

                    ProductItem item = new ProductItem(name, URL, cod, quantity, price, null);
                    data.add(item);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private void saveBasketToFile(String FILENAME, ArrayList<ProductItem> data) {
        ArrayList<String> text = new ArrayList<>();
        for(ProductItem item : data) {
            String tmp = item.getName() + "," +
                    item.getURL() + "," +
                    item.getCod() + "," +
                    item.getQuantity() + "," +
                    item.getPrice() + "," +
                    item.getExpDate() + "\n";

            text.add(tmp);
        }

        FileOutputStream fos = null;
        File file = new File(MainActivity.FILE_PATH + FILENAME);

        try {
            if(!file.exists())
                if(!file.createNewFile())
                    Log.e(TAG, "saveBasketToFile: file creation failed" + FILENAME);

            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            for(String str : text)
                fos.write(str.getBytes());
            Log.d(TAG, "onPause: Files saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
