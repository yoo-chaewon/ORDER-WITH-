package com.example.order_with.Data

import android.os.Parcel
import android.os.Parcelable


data class Menu(
    val name: String,
    val num: Int,
    val price: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString().toString(),
        source.readInt(),
        source.readString().toString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeInt(num)
        writeString(price)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Menu> = object : Parcelable.Creator<Menu> {
            override fun createFromParcel(source: Parcel): Menu = Menu(source)
            override fun newArray(size: Int): Array<Menu?> = arrayOfNulls(size)
        }
    }
}
