package com.example.licentatest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ProductUpdater extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {
    private static final String TAG = "ProductUpdater";

    BTHandler bluetoothIn;
    private ConnectedThread mConnectedThread;

    private BluetoothAdapter mBTAdapter = null;
    private BluetoothSocket mBTSocket = null;

    int handlerState = 0;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String address;
    private Context mContext;

    Handler mDataHandler = new Handler();
    Runnable mRunnable;

    private static final String RECIPESMAIN = "getRecipes";
    private static final String RECIPES = "getRecipe";

    public ProductUpdater(Context context, String address) {
        this.address = address;
        this.mContext = context;
        bluetoothIn = new BTHandler(Looper.getMainLooper(), context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected ArrayList<ArrayList<String>> doInBackground(String... strings) {
        if(!DeviceListActivity.connection)
            mContext.startActivity(new Intent(mContext, MainActivity.class));

        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

        try {
            mBTSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Log.e(TAG, "onResume: creating BT socket failed");
        }

        try {
            mBTSocket.connect();
            if(mBTSocket.isConnected()) {
                mContext.startActivity(new Intent(mContext, MainActivity.class));
            } else {
                DeviceListActivity.connection = false;
                mContext.startActivity(new Intent(mContext, MainActivity.class));
            }
        } catch (IOException e) {
            Log.e(TAG, "onResume: socket connect failed");
            try {
                mBTSocket.close();
                Intent intent = new Intent(mContext, DeviceListActivity.class);
                mContext.startActivity(intent);
            } catch (IOException e1) {
                Log.e(TAG, "onResume: socket closing after connect failed, failed");
            }
        }

        mConnectedThread = new ConnectedThread(mBTSocket);
        mConnectedThread.start();

        String send = "x ";
        send += Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        send += "\n";
        mConnectedThread.write(send);

        RecipeDatabase recipeDatabase = RecipeDatabase.getHelper(mContext);
        Dao<Recipe, Integer> recipeDao = recipeDatabase.getRecipeDao();

        RawRowMapper<String> mapper = new RawRowMapper<String>() {
            @Override
            public String mapRow(String[] strings, String[] strings1) throws SQLException {
                return strings1[0];
            }
        };

        try {
            ArrayList<String> mRecipeNames;
            ArrayList<String> mRecipeTypes;

            GenericRawResults<String> nume = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("name")
                    .prepareStatementString(), mapper);
            List<String> tmp = nume.getResults();
            mRecipeNames = new ArrayList<>(tmp);

            GenericRawResults<String> type = recipeDao.queryRaw(recipeDao.queryBuilder()
                    .selectColumns("type")
                    .prepareStatementString(), mapper);
            List<String> types = type.getResults();
            mRecipeTypes = new ArrayList<>(types);


            if(!mRecipeNames.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder("r,");

                for (int pos = 0; pos < mRecipeNames.size(); pos++) {
                    stringBuilder.append(mRecipeNames.get(pos));
                    stringBuilder.append(".");
                    stringBuilder.append(mRecipeTypes.get(pos));
                    stringBuilder.append(",");
                }

                String tmp1 = stringBuilder.toString();

                if (!tmp1.equals("r,")) {
                    mConnectedThread.write(tmp1);
                }

                Log.d(TAG, "onReceive: Recipes received");
            }
        } catch(SQLException e){
            Log.e(TAG, "recipe init: failed");
        }

        recipeDatabase.close();

        return null;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: InputStream failed init");
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e1) {
                Log.e(TAG, "ConnectedThread: OutputStream failed init");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);

                    if (bluetoothIn != null)
                        bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "reading failed");
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();

            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.e(TAG, "write: failed");
                //finish();
            }

        }
    }
}
