package com.example.licentatest;

import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final String TAG = "DatabaseConfigUtil";

    private static final Class<?>[] classes = new Class[]{Product.class, Recipe.class};

    public static void main(String[] args) {
        try {
            writeConfigFile("ormlite_config.txt", classes);
        } catch (SQLException e) {
            Log.e(TAG, "main: file config failed sql");
        } catch (IOException e) {
            Log.e(TAG, "main: file config failed io");
        }
    }
}
