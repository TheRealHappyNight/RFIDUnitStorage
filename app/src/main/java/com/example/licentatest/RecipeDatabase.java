package com.example.licentatest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class RecipeDatabase extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "RecipeDatabase";

    private Dao<Recipe, Integer> recipeDao = null;

    private static final String DATABASE_NAME = "Recipes.db";
    private static final int DATABASE_VERSION = 1;

    private static RecipeDatabase helper = null;
    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private RecipeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    public static synchronized RecipeDatabase getHelper(Context context) {
        if(helper == null) {
            helper = new RecipeDatabase(context);
        }
        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Recipe.class);
        }catch (SQLException e) {
            Log.e(TAG, "onCreate: table creating failed");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource, Recipe.class, true);
        } catch (SQLException e) {
            Log.e(TAG, "onUpgrade: table drop failed");
        }

        onCreate(sqLiteDatabase, connectionSource);
    }

    public Dao<Recipe, Integer> getRecipeDao() {
        if (recipeDao == null) {
            try {
                recipeDao = getDao(Recipe.class);
            } catch (SQLException e) {
                Log.e(TAG, "getRecipeDao: failed");
            }
        }
        return recipeDao;
    }

    @Override
    public void close() {
        if(usageCounter.decrementAndGet() == 0) {
            super.close();
            recipeDao = null;
            helper = null;
        }
    }
}
