package com.example.licentatest;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@DatabaseTable(tableName = "Products")

public class Product {

    @DatabaseField(generatedId = true)
    private int Id;
    @DatabaseField(columnName = "produs",canBeNull = false)
    private String Name;
    @DatabaseField(columnName = "cod", canBeNull = false)
    private String UUID;
    @DatabaseField(columnName = "url", canBeNull = false)
    private String ImageURL;
    @DatabaseField
    private int Quantity;
    @DatabaseField(columnName = "pret")
    private double Price;
    @DatabaseField(columnName = "data")
    private String ExpDate;

    public Product() {
    }

//    public Product(String name, String UUID, String imageURL, String expDate, int quantity) {
//        super();
//        Name = name;
//        this.UUID = UUID;
//        ImageURL = imageURL;
//        Quantity = quantity;
//        ExpDate = expDate;
//    }


    public Product(String name, String UUID, String imageURL, int quantity, double price, String expDate) {
        Name = name;
        this.UUID = UUID;
        ImageURL = imageURL;
        Quantity = quantity;
        Price = price;
        ExpDate = expDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", UUID='" + UUID + '\'' +
                ", ImageURL='" + ImageURL + '\'' +
                ", Quantity=" + Quantity +
                ", Price=" + Price +
                ", ExpDate='" + ExpDate + '\'' +
                '}';
    }
}
