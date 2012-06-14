package com.datamyne.mobile.profile.utils;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Contenedor de datos.
 */
public class Item implements Parcelable{
	String code;
	String name;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Item() {
		super();
	}
	public Item(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	public int describeContents() {
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
	}

     public static final Parcelable.Creator<Item> CREATOR
             = new Parcelable.Creator<Item>() {
         public Item createFromParcel(Parcel in) {
             return new Item(in);
         }

         public Item[] newArray(int size) {
             return new Item[size];
         }
     };
     
     private Item(Parcel in) {
        this.code = in.readString();
		this.name = in.readString();
     }
	
}
