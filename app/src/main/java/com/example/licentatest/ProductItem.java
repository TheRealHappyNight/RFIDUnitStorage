package com.example.licentatest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArraySet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProductItem implements Parcelable {
    private static final String TAG = "ProductItem";

    private String Name;
    private String URL;
    private String Cod;
    private String Quantity;
    private double Price;
    private String ExpDate;

    public ProductItem() {
    }

    public ProductItem(String name, String URL, String cod, String quantity, double price, String expDate) {
        Name = name;
        this.URL = URL;
        Cod = cod;
        Quantity = quantity;
        Price = price;
        ExpDate = expDate;
    }

    public ProductItem(Parcel parcel) {
        Name = parcel.readString();
        URL = parcel.readString();
        Cod = parcel.readString();
        Quantity = parcel.readString();
        Price = parcel.readDouble();
        ExpDate = parcel.readString();
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCod() {
        return Cod;
    }

    public void setCod(String cod) {
        this.Cod = cod;
    }

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    public String getQuantity() {
        return Quantity;
    }

    public boolean equals(ProductItem obj) {
        return Name.equals(obj.Name) &&
                URL.equals(obj.URL) &&
                Cod.equals(obj.Cod) &&
                (Price == obj.Price) &&
                Quantity.equals(obj.Quantity) &&
                ExpDate.equals(obj.ExpDate);
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(URL);
        dest.writeString(Cod);
        dest.writeString(Quantity);
        dest.writeDouble(Price);
        dest.writeString(ExpDate);
    }

    public static final Parcelable.Creator<ProductItem> CREATOR = new Parcelable.Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel source) {
            return new ProductItem(source);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[0];
        }
    };

    @Override
    public String toString() {
        return "ProductItem{" +
                "Name='" + Name + '\'' +
                ", URL='" + URL + '\'' +
                ", Cod='" + Cod + '\'' +
                ", Quantity='" + Quantity + '\'' +
                ", Price=" + Price +
                ", ExpDate='" + ExpDate + '\'' +
                '}';
    }
}