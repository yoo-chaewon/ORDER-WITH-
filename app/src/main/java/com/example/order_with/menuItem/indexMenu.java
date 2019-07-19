package com.example.order_with.menuItem;

import android.os.Parcel;
import android.os.Parcelable;

public class indexMenu implements Parcelable {
    private String Character;
    private int count;

    public indexMenu(String Character, int count) {
        this.Character = Character;
        this.count = count;
    }

    public String getCharacter() {
        return Character;
    }

    public void setCharacter(String Character) {
        this.Character = Character;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    protected indexMenu(Parcel in) {
        Character = in.readString();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Character);
        dest.writeInt(count);
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


    @Override
    public String toString() {
        return "indexMenu{" +
                "Character='" + Character + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}