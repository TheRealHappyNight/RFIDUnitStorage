package com.example.licentatest;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Recipes")
public class Recipe implements Parcelable {

    @DatabaseField(generatedId = true, columnName = "id")
    int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String imageURL;
    @DatabaseField
    private String description;
    @DatabaseField
    private String products;
    @DatabaseField
    private Integer type;

    public Recipe() {
    }

    public Recipe(String name, String imageURL, String description, String products) {
        this.name = name;
        this.imageURL = imageURL;
        this.description = description;
        this.products = products;
    }

    public Recipe(String name, String imageURL, String description, String products, Integer type) {
        this.name = name;
        this.imageURL = imageURL;
        this.description = description;
        this.products = products;
        this.type = type;
    }

    public Recipe(Parcel parcel) {
        name = parcel.readString();
        imageURL = parcel.readString();
        description = parcel.readString();
        products = parcel.readString();
        type = parcel.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", description='" + description + '\'' +
                ", products='" + products + '\'' +
                ", type=" + type +
                '}';
    }

    public String toSendString() {
        return name + " " + Integer.toString(type);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageURL);
        dest.writeString(description);
        dest.writeString(products);
        dest.writeInt(type);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[0];
        }
    };
}
