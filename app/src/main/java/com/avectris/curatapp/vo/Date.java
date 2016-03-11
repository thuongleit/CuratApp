package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class Date implements Parcelable {
    @JsonProperty("date")
    String mDate;

    @JsonProperty("timezone_type")
    int mTimeZoneType;

    @JsonProperty("timezone")
    String mTimeZone;

    public Date() {
    }

    private Date(Parcel in) {
        this.mDate = in.readString();
        this.mTimeZoneType = in.readInt();
        this.mTimeZone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDate);
        dest.writeInt(this.mTimeZoneType);
        dest.writeString(this.mTimeZone);
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public int getTimeZoneType() {
        return mTimeZoneType;
    }

    public void setTimeZoneType(int timeZoneType) {
        this.mTimeZoneType = timeZoneType;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        this.mTimeZone = timeZone;
    }

    public static final Creator<Date> CREATOR = new Creator<Date>() {
        public Date createFromParcel(Parcel source) {
            return new Date(source);
        }

        public Date[] newArray(int size) {
            return new Date[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Date date = (Date) o;

        return mDate != null ? mDate.equals(date.mDate) : date.mDate == null;

    }
}
