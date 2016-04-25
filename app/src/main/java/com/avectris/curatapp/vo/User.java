package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;
import com.avectris.curatapp.data.local.CuratAppDatabase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by thuongle on 4/25/16.
 */
@Table(database = CuratAppDatabase.class)
public class User extends BaseModel implements Parcelable {

    @Column
    @PrimaryKey
    public String email;

    @Column
    @JsonProperty("auth_token")
    public String authToken;

    @Column(defaultValue = "false")
    public boolean isActive;

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.authToken);
        dest.writeByte(isActive ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.authToken = in.readString();
        this.isActive = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
