package com.example.licentatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BasketActivity extends AppCompatActivity {
    private static final String TAG = "BasketActivity";

    private static final String FILE_NAME = "Basket.txt";
    ArrayList<ProductItem> mBasket;

    ProductRecyclerViewAdapter recyclerViewAdapter;

    ImageButton mBuyButton;
    Button mResetButton;
    TextView priceView;

    //Looping vars
    Handler mDataHandler = new Handler();
    Runnable mRunnable;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(BasketActivity.this, MainActivity.class));
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        mBuyButton = findViewById(R.id.orderButton);
        mResetButton = findViewById(R.id.resetBasketButton);
        priceView = findViewById(R.id.basketPrice);

        mBasket = loadFromFile(FILE_NAME);

        int sum = 0;
        for(ProductItem item : mBasket) {
            sum += item.getPrice();
        }
        priceView.setText(Integer.toString(sum));

        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BasketActivity.this, "Long press to reset", Toast.LENGTH_SHORT).show();
            }
        });

        mResetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mBasket.clear();
                File file = new File(MainActivity.FILE_PATH + FILE_NAME);
                if(file.exists())
                    if(file.delete()) {
                        Log.d(TAG, FILE_NAME + "onLongClick: file deleted");
                        initRecyclerView(mBasket);
                        Toast.makeText(BasketActivity.this, "Resetare completă", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Log.e(TAG, FILE_NAME + "onLongClick: file delete failed");
                return false;
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
        if(mBasket != null)
            initRecyclerView(mBasket);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        saveBasketToFile(FILE_NAME);
        super.onDestroy();
    }

    private void initRecyclerView(ArrayList<ProductItem> tmp) {
        Log.d(TAG, "initRecyclerView: init RecyclerView");

        RecyclerView recyclerView = findViewById(R.id.basketListView);
        recyclerViewAdapter = new ProductRecyclerViewAdapter(tmp, this);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public ArrayList<ProductItem> loadFromFile(String FILENAME) {
        ArrayList<ProductItem> basket = new ArrayList<>();
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
                    String Date = stringTokenizer.nextToken();

                    ProductItem item = new ProductItem(name, URL, cod, quantity, price, Date);

                    boolean add = true;
                    for(ProductItem i : basket) {
                        if(i.getName().equals(item.getName())) {
                            i.setQuantity(Integer.toString(Integer.parseInt(i.getQuantity()) + 1));
                            add = false;
                            break;
                        }
                    }

                    if(add)
                        basket.add(item);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return basket;
    }

    private void saveBasketToFile(String FILENAME) {
        ArrayList<String> text = new ArrayList<>();
        for(ProductItem item : mBasket) {
            StringBuilder data = new StringBuilder(item.getName() + "," +
                    item.getURL() + "," +
                    item.getCod() + "," +
                    item.getQuantity() + "," +
                    item.getPrice() + "," +
                    item.getExpDate() + "\n");

            text.add(data.toString());
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
                return false;
            }
        });

        return true;
    }
}
