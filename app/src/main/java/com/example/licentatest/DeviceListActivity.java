package com.example.licentatest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private static final String TAG = "DeviceListActivity";

    TextView textConnectionStatus;
    ListView pairedListView;
    Button noConnectionButton;

    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    public static boolean connection;

    public static ArrayList<String> BTDebug = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        textConnectionStatus = findViewById(R.id.connecting);
        textConnectionStatus.setTextSize(40);

        mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        noConnectionButton = findViewById(R.id.offlineButton);
        noConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection = false;
                startActivity(new Intent(DeviceListActivity.this, MainActivity.class));
            }
        });

        connection = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkBTState();

        mPairedDevicesArrayAdapter.clear();

        textConnectionStatus.setText(" ");

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();

        if(pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for(BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        else {
            mPairedDevicesArrayAdapter.add("No Devices");
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            textConnectionStatus.setText("Connecting...");

            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            new ProductUpdater(DeviceListActivity.this, address).execute();
        }
    };

    private void checkBTState() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBTAdapter == null) {
            Log.e(TAG, "checkBTState: Bluetooth not supported. Closing");
            Toast.makeText(getBaseContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(!mBTAdapter.isEnabled()) {
            Intent startBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(startBT, 1);
        }
    }
}
