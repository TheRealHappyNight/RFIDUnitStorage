package com.example.licentatest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.query.In;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextClock datetime;
    TextView alarmView;
    ImageButton productListBtn;
    ImageButton recipeListBtn;
    ImageButton basketBtn;
    Button financeBtn;

    BluetoothAdapter mBTAdapter = null;

    ArrayList<ProductItem> mInventory;
    ArrayList<ProductItem> mBasket;
    public static final String FILE_PATH = "/data/user/0/com.example.licentatest/files/";
//    private static final String BASKET = "BASKET";
//    private static final String SENDBASKET = "Send Basket";
    public static final String INVENTORIES = "Inventories";
    private static final String INVENTORY = "Inventory";

    Handler mDataHandler = new Handler();
    Runnable mRunnable;
    public static int mDataDelay = 500; // 5 sec

    //Debug
    TextView txtString, txtStringLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBTState();

        datetime = findViewById(R.id.datetimeView);
        alarmView = findViewById(R.id.alarmView);
        productListBtn = findViewById(R.id.productListButton);
        recipeListBtn = findViewById(R.id.recipeListButton);
        basketBtn = findViewById(R.id.basketButton);
        financeBtn = findViewById(R.id.financeButton);

        //Link the buttons with respective views
        txtString = findViewById(R.id.txtString);
        txtStringLength = findViewById(R.id.testView1);

//        mInventory = new ArrayList<>();
//        mBasket = getIntent().getParcelableArrayListExtra(SENDBASKET);

        if(mBasket == null)
            mBasket = new ArrayList<>();

        productListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProductListActivity.class));
            }
        });

        recipeListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecipeListActivity.class));
            }
        });

        basketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BasketActivity.class));
            }
        });

        financeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Finance.class));
            }
        });
    }

    private BroadcastReceiver mGetInventory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mInventory = intent.getParcelableArrayListExtra(INVENTORY);
            Log.d(TAG, "onReceive: Inventories received");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mDataHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                if(!DeviceListActivity.BTDebug.isEmpty()) {
                    txtString.setText(DeviceListActivity.BTDebug.get(0));
                    txtStringLength.setText(DeviceListActivity.BTDebug.get(1));

                    LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mGetInventory, new IntentFilter(INVENTORIES));
                    if(mInventory != null && !mInventory.isEmpty())
                        updateAlarm();
                }
                mDataHandler.postDelayed(mRunnable, mDataDelay);
            }
        }, 100);
    }

    void updateAlarm() {
        try {
            Calendar cal = Calendar.getInstance();
            String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(cal.getTimeInMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date now = sdf.parse(currentDate);
            sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date min = sdf.parse(mInventory.get(0).getExpDate());
            int pos = Integer.MAX_VALUE;
            long days = 0;


            if(mInventory.size() == 1) {
                pos = 0;
                long tmp1 = min.getTime() - now.getTime();
                days = TimeUnit.MILLISECONDS.toDays(tmp1);
            }
            else {
                for (int i = 0; i < mInventory.size(); i++) {
                    Date tmp = sdf.parse(mInventory.get(i).getExpDate());
                    if (min.after(tmp)) {
                        min = tmp;
                        pos = i;
                        long tmp1 = tmp.getTime() - now.getTime();
                        days = TimeUnit.MILLISECONDS.toDays(tmp1);
                    }
                }
            }

            if(pos != Integer.MAX_VALUE) {
                String info = mInventory.get(pos).getName() + " " + Long.toString(days) + " zile";
                if (days <= 2)
                    alarmView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                else
                    alarmView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));

                alarmView.setText(info);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void checkBTState() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTAdapter == null) {
            Log.e(TAG, "checkBTState: Bluetooth not supported. Closing");
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!mBTAdapter.isEnabled()) {
            Intent startBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(startBT, 1);
        }
    }
}