package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class Agency implements Parcelable {

    @JsonProperty("name")
    String name;

    public Agency() {
    }

    private Agency(Parcel in) {
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final Creator<Agency> CREATOR = new Creator<Agency>() {
        public Agency createFromParcel(Parcel source) {
            return new Agency(source);
        }

        public Agency[] newArray(int size) {
            return new Agency[size];
        }
    };
}
