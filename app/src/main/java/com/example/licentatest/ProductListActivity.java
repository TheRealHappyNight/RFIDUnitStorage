package com.example.licentatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ProductListActivity extends AppCompatActivity {
    private static final String TAG = "ProductListActivity";

    ProductRecyclerViewAdapter recyclerViewAdapter;
    ProductRecyclerViewAdapter recyclerViewAdapter2;

    private static final String INVENTORIES = "Inventories";
    private static final String INVENTORY = "Inventory";
    private static final String MISSINVENTORY = "Missing Inventory";
    ArrayList<ProductItem> mInventory;
    ArrayList<ProductItem> mMissInventory;

    //Looping vars
    Handler mDataHandler = new Handler();
    Runnable mRunnable;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(ProductListActivity.this, MainActivity.class));
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mInventory = new ArrayList<>();
        mMissInventory = new ArrayList<>();
    }

    private BroadcastReceiver mGetInventory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mInventory = intent.getParcelableArrayListExtra(INVENTORY);
            mMissInventory = intent.getParcelableArrayListExtra(MISSINVENTORY);
            Log.d(TAG, "onReceive: Inventories received");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mDataHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager.getInstance(ProductListActivity.this).registerReceiver(mGetInventory, new IntentFilter(INVENTORIES));
                if(mInventory != null && mMissInventory != null)
                    updateValues();

                mDataHandler.postDelayed(mRunnable, MainActivity.mDataDelay);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGetInventory);
        mDataHandler.removeCallbacks(mRunnable);

        super.onDestroy();
    }

    private void updateValues() {
        Log.d(TAG, "updateValues: preparing bitmaps");

        if(!mInventory.isEmpty()) {
            mInventory.get(0).setName("Fara produse");
            mInventory.get(0).setURL("http://clipartbarn.com/wp-content/uploads/2017/09/Happy-and-sad-face-clip-art-free-clipart-images.jpeg");
            mInventory.get(0).setQuantity("0");
            mInventory.get(0).setPrice(-1.2390);
            mInventory.get(0).setExpDate("Niciodată");
        }

        initRecyclerView();
    }



    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init RecyclerView");

        RecyclerView recyclerView = findViewById(R.id.productListView);
        recyclerViewAdapter = new ProductRecyclerViewAdapter(mInventory,this);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView recyclerView2 = findViewById(R.id.productMissListView);
        recyclerViewAdapter2 = new ProductRecyclerViewAdapter(mMissInventory,this);

        recyclerView2.setAdapter(recyclerViewAdapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerViewAdapter.getFilter().filter(s);
                recyclerViewAdapter2.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }
}
