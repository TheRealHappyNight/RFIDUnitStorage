package com.example.licentatest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class ProductDatabase extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "ProductDatabase";

    private Dao<Product, Integer> productDao = null;
    private RuntimeExceptionDao<Product, Integer> productRuntimeExceptionDao = null;

    private static final String DATABASE_NAME = "Products.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Product.class);
        }catch (SQLException e) {
            Log.e(TAG, "onCreate: table creating failed");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource, Product.class, true);
        } catch (SQLException e) {
            Log.e(TAG, "onUpgrade: table drop failed");
        }

        onCreate(sqLiteDatabase, connectionSource);
    }

    public Dao<Product, Integer> getProductDao() {
        if (productDao == null) {
            try {
                productDao = getDao(Product.class);
            } catch (SQLException e) {
                Log.e(TAG, "getProductDao: failed");
            }
        }
        return productDao;
    }

    public RuntimeExceptionDao<Product, Integer> getProductRuntimeExceptionDao() {
        if(productRuntimeExceptionDao == null) {
            try {
                productRuntimeExceptionDao = getRuntimeExceptionDao(Product.class);
            } catch (RuntimeException e) {
                Log.e(TAG, "getProductRuntimeExceptionDao: failed");
            }
        }
        
        return productRuntimeExceptionDao;
    }
}
