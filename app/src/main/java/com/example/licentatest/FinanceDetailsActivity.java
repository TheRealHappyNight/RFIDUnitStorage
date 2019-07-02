package com.example.licentatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class FinanceDetailsActivity extends AppCompatActivity {
    private static final String TAG = "FinanceDetailsActivity";

    private static final String FILE_NAME = "Finance_Record.txt";
    public static final String INVENTORIES = "Inventories";
    public static final String INVENTORY = "Inventory";
    ArrayList<ProductItem> mInventory;

    public static boolean reset = false;

    private int totalPrice = 0;

    Handler mDataHandler = new Handler();
    Runnable mRunnable;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_details);

        //mInventory = new ArrayList<>();
        load();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
    }

    public void load() {
        mInventory = new ArrayList<>();
        FileInputStream fis = null;
        File file = new File(MainActivity.FILE_PATH + FILE_NAME);

        if(file.exists()) {
            try {
                fis = openFileInput(FILE_NAME);
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
                    String Date = stringTokenizer.nextToken();

                    ProductItem item = new ProductItem(name, URL, cod, quantity, price, Date);
                    mInventory.add(item);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        if(reset) {
            if (mInventory != null) {
                mInventory.clear();
            }
            File file = new File(MainActivity.FILE_PATH + FILE_NAME);
            if(file.exists())
                if(file.delete())
                    Log.d(TAG, "run: file deleted" + FILE_NAME);
                else
                    Log.e(TAG, "run: file delete failed" + FILE_NAME);
            reset = false;
        }

        mDataHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager.getInstance(FinanceDetailsActivity.this).registerReceiver(mGetInventory, new IntentFilter(INVENTORIES));
                if(mInventory != null)
                    initRecyclerView();
                mDataHandler.postDelayed(mRunnable, MainActivity.mDataDelay);
            }
        }, 100);
        super.onResume();
    }

    public static BroadcastReceiver mDoReset = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reset = true;
        }
    };

    private BroadcastReceiver mGetInventory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<ProductItem> newInventory = intent.getParcelableArrayListExtra(INVENTORY);

            for(ProductItem newItem : newInventory) {
                boolean add = true;
                for(int pos = 0; pos < mInventory.size(); pos++) {
                    if(mInventory.get(pos).getCod().equals(newItem.getCod())) {
                        add = false;
                    }

                    if(add && mInventory.get(pos).getName().equals(newItem.getName())) {
                        mInventory.get(pos).setQuantity(Integer.toString(
                                Integer.parseInt(mInventory.get(pos).getQuantity()) +
                                        Integer.parseInt(newItem.getQuantity())));
                    }
                }

                if(add) {
                    Calendar cal = Calendar.getInstance();
                    String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(cal.getTimeInMillis());
                    newItem.setExpDate(currentDate);
                    mInventory.add(newItem);
                }
            }
            Log.d(TAG, "onReceive: Inventories received");

            int lastPrice = totalPrice;
            totalPrice = 0;
            for(ProductItem item : mInventory) {
                totalPrice += item.getPrice();
            }

            if(lastPrice != totalPrice) {
                mEditor.putInt(Finance.CURRCYCLESPENT, totalPrice);
                mEditor.apply();
            }
        }
    };

    protected void onDestroy() {
        ArrayList<String> text = new ArrayList<>();
        for(ProductItem item : mInventory) {
            StringBuilder data = new StringBuilder(item.getName() + "," +
                    item.getURL() + "," +
                    item.getCod() + "," +
                    item.getQuantity() + "," +
                    item.getPrice() + "," +
                    item.getExpDate() + "\n");

            text.add(data.toString());
        }

        FileOutputStream fos = null;
        File file = new File(MainActivity.FILE_PATH + FILE_NAME);

        try {
            if(!file.exists())
                if (!file.createNewFile()) {
                    Log.e(TAG, "onDestroy: file creation failed" + FILE_NAME);
                }
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            for (String str : text)
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

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGetInventory);
        mDataHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init RecyclerView");

        RecyclerView recyclerView = findViewById(R.id.productListView);
        ProductRecyclerViewAdapter recyclerViewAdapter = new ProductRecyclerViewAdapter(mInventory, this);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
