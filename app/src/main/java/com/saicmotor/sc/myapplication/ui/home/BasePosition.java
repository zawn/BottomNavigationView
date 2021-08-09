
package com.saicmotor.sc.myapplication.ui.home;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BasePosition implements Parcelable, Serializable {

    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    public final static Creator<BasePosition> CREATOR = new Creator<BasePosition>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BasePosition createFromParcel(android.os.Parcel in) {
            return new BasePosition(in);
        }

        public BasePosition[] newArray(int size) {
            return (new BasePosition[size]);
        }

    };

    protected BasePosition(android.os.Parcel in) {
        this.longitude = ((double) in.readValue((double.class.getClassLoader())));
        this.latitude = ((double) in.readValue((double.class.getClassLoader())));
    }

    public BasePosition() {
    }

    public BasePosition(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(longitude);
        dest.writeValue(latitude);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "BasePosition{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
