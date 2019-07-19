package com.example.order_with.menuItem;

import android.os.Parcel;
import android.os.Parcelable;

public class serverMenu implements Parcelable{
    int num;
    private String title;
    private String price;

    public serverMenu(String title, String price) {
        this.num = num;
        this.title = title;
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    protected serverMenu(Parcel in) {
        title = in.readString();
        price = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Menu{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
