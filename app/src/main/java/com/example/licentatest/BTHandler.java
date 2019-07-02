package com.example.licentatest;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

public class BTHandler extends Handler {
    private static final String TAG = "BTHandler";

    private static final String INV_FILE_NAME = "INVENTORY.txt";
    private static final String MISS_INV_FILE_NAME = "MISSINVENTORY.txt";
    private static final String INVENTORIES = "Inventories";
    private static final String INVENTORY = "Inventory";
    private static final String MISSINVENTORY = "Missing Inventory";
    private ArrayList<ProductItem> mInventory;
    private ArrayList<ProductItem> mMissInventory;

    private ArrayList<String> mProductCodes;
    
    private StringBuilder recDataString = new StringBuilder();

    private RuntimeExceptionDao<Product, Integer> productDao;
    private ProductDatabase productDB;
    private Context mContext;

    public BTHandler(Looper looper, Context context) {
        super(looper);
        mProductCodes = new ArrayList<>();
        mInventory = new ArrayList<>();
        mMissInventory = new ArrayList<>();

        this.mContext = context;

        if(!DeviceListActivity.connection)
            verifyProductsWithoutBT(context);

        //customCommands(context);
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        super.handleMessage(msg);
        if (msg.what == 0) {
            String readMessage = (String) msg.obj;
            recDataString.append(readMessage);

            int endOfLineIndex = recDataString.indexOf("~");
            if (endOfLineIndex > 0) {
                String dataInPrint = recDataString.substring(0, endOfLineIndex);
                int dataLength = dataInPrint.length();
                DeviceListActivity.BTDebug.clear();
                DeviceListActivity.BTDebug.add(dataInPrint);
                DeviceListActivity.BTDebug.add(Integer.toString(dataLength));

                if (recDataString.charAt(0) == '#') {
                    int productCount = dataLength / 8;
                    mProductCodes.clear();

                    for (int i = 0; i < productCount; i++) {
                        String tmp = recDataString.substring(1 + (9 * i), 9 + (9 * i));

                        mProductCodes.add(tmp);
                    }
                }

                recDataString.delete(0, recDataString.length());
                dataInPrint = " ";

                verifyProductsWithBT(mContext);
            }
        }
    }

    private void customCommands(Context context) {
        productDB = OpenHelperManager.getHelper(context, ProductDatabase.class);
        productDao = productDB.getProductRuntimeExceptionDao();

//        UpdateBuilder<Product, Integer> updateProductDao = productDao.updateBuilder();
//        try {
//            updateProductDao.where().eq("produs", "Milk");
//            updateProductDao.updateColumnValue("Quantity", 8);
//            updateProductDao.update();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        try {
//            DeleteBuilder<Product, Integer> deleteBuilder = productDao.deleteBuilder();
//            Where<Product, Integer> where;
//            where = productDao.deleteBuilder().where().
//                    eq("Id", 1).and().eq("produs", "Milk");
//            deleteBuilder.setWhere(where);
//            deleteBuilder.delete();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        try {
            TableUtils.dropTable(productDao, true);
            TableUtils.createTable(productDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        nume, uuid, url, expdate, cantitate
        productDao.createOrUpdate(new Product("Lapte",
                "84A74D1E",
                "https://cdn.shopify.com/s/files/1/0206/9470/products/southcoast-milk-1l_1024x1024.jpg?v=1494139427",
                1,
                2.99,
                "10.06.2019"));
        productDao.createOrUpdate(new Product("Cascaval",
                "CA19380B",
                "https://www.paxtonandwhitfield.co.uk/media/image/09/bb/3f/image_1_166M4kG9bPtSQhcN_600x600.jpg",
                1,
                10.35,
                "10.06.2019"));
        productDao.createOrUpdate(new Product("Oua",
                "667AD773",
                "https://nnimgt-a.akamaihd.net/transform/v1/crop/frm/GJZ5TVpAk84wrTzsQfLQRB/902a6862-5fe6-4603-84d0-eff78a6499f2.jpg/r0_51_1000_613_w1200_h678_fmax.jpg",
                1,
                9.49,
                "10.06.2019"));
        productDao.create(new Product("Salam",
                "",
                "https://www.cora.ro/images/products/2465629/gallery/2465629_hd_1.jpg",
                1,
                13.69,
                "10.06.2019"));
        productDao.create(new Product("Iaurt",
                "",
                "https://s12emagst.akamaized.net/products/730/729614/images/res_38eba722add52e48c9dd840fff31ef11_full.jpg",
                1,
                2.84,
                "10.06.2019"));

        List<Product> a = productDao.queryForAll();

        Log.e(TAG, "createProducts: " + a.toString());

        OpenHelperManager.releaseHelper();
    }

    private void verifyProductsWithBT(Context mContext) {
        productDB = OpenHelperManager.getHelper(mContext, ProductDatabase.class);
        productDao = productDB.getProductRuntimeExceptionDao();

        mInventory.clear();
        mMissInventory.clear();

        RawRowMapper<String> mapper = new RawRowMapper<String>() {
            @Override
            public String mapRow(String[] strings, String[] strings1) throws SQLException {
                return strings1[0];
            }
        };

        try {
            GenericRawResults<String> cod = productDao.queryRaw(productDao.queryBuilder()
                    .selectColumns("cod")
                    .prepareStatementString(), mapper);

            if(cod != null) {
                GenericRawResults<String> nume = productDao.queryRaw(productDao.queryBuilder()
                        .selectColumns("produs")
                        .prepareStatementString(), mapper);
                List<String> Nume = nume.getResults();

                GenericRawResults<String> url = productDao.queryRaw(productDao.queryBuilder()
                        .selectColumns("url")
                        .prepareStatementString(), mapper);
                List<String> URL = url.getResults();

                GenericRawResults<String> pret = productDao.queryRaw(productDao.queryBuilder()
                        .selectColumns("pret")
                        .prepareStatementString(), mapper);
                List<String> Pret = pret.getResults();

                GenericRawResults<String> expdate = productDao.queryRaw(productDao.queryBuilder()
                        .selectColumns("data")
                        .prepareStatementString(), mapper);
                List<String> ExpDate = new ArrayList<>();
                try {
                    ExpDate = expdate.getResults();
                } catch(Exception e){
                    Log.e(TAG, "verifyProducts: expdate.getResults() failed");
                }

                Map<String, Integer> duplicates = new HashMap<>();
                int pos = -1;
                ArrayList<String> coduri = new ArrayList<>();
                for(String i : cod) {
                    coduri.add(i);
                    ++pos;
                    if(mProductCodes.contains(i.toLowerCase())) {
                        Integer j = duplicates.get(Nume.get(pos));
                        duplicates.put(Nume.get(pos), (j == null) ? 1 : j + 1);
                    }
                }

                pos = -1;
                for(String i : coduri) {
                    ++pos;
                    ProductItem tmp = new ProductItem(Nume.get(pos),
                            URL.get(pos),
                            i,
                            "1",
                            Double.parseDouble(Pret.get(pos)),
                            ExpDate.get(pos));

                    for(Map.Entry<String, Integer> val : duplicates.entrySet()) {
                        if(val.getKey().equals(Nume.get(pos))) {
                            tmp.setQuantity(val.getValue().toString());
                        }
                    }

                    if(!ExpDate.isEmpty())
                        tmp.setExpDate(ExpDate.get(pos));

                    if(mProductCodes.contains(i.toLowerCase())) {
                        mInventory.add(tmp);
                    } else {
                        tmp.setQuantity("0");
                        mMissInventory.add(tmp);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "verifyProducts: failed");
        }

        Log.d(TAG, "verifyProducts: Sending inventories");
        Intent intent = new Intent(INVENTORIES);
        intent.putParcelableArrayListExtra(INVENTORY ,mInventory);
        intent.putParcelableArrayListExtra(MISSINVENTORY, mMissInventory);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        saveBasketToFile(INV_FILE_NAME, mInventory);
        saveBasketToFile(MISS_INV_FILE_NAME, mMissInventory);

        OpenHelperManager.releaseHelper();
    }

    private void verifyProductsWithoutBT(Context mContext) {
        mInventory = loadFromFile(INV_FILE_NAME);
        mMissInventory = loadFromFile(MISS_INV_FILE_NAME);

        Log.d(TAG, "verifyProducts: Sending inventories");
        Intent intent = new Intent(INVENTORIES);
        intent.putParcelableArrayListExtra(INVENTORY ,mInventory);
        intent.putParcelableArrayListExtra(MISSINVENTORY, mMissInventory);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private ArrayList<ProductItem> loadFromFile(String FILENAME) {
        ArrayList<ProductItem> data = new ArrayList<>();
        FileInputStream fis;
        File file = new File(MainActivity.FILE_PATH + FILENAME);

        if (file.exists()) {
            try {
                fis = mContext.openFileInput(FILENAME);
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

            fos = mContext.openFileOutput(FILENAME, MODE_PRIVATE);
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
