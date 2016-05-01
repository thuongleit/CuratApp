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
 * Created by thuongle on 2/12/16.
 */
@Table(database = CuratAppDatabase.class, name = "account")
public class Account extends BaseModel implements Parcelable {

    @Column()
    @PrimaryKey()
    @JsonProperty("id")
    public String id;

    @Column(name = "name")
    @JsonProperty("name")
    public String name;

    @Column(name = "apiCode")
    @JsonProperty("api_code")
    public String apiCode;

    @Column(name = "active")
    @JsonProperty("active")
    public int active;

    @Column(defaultValue = "false")
    public boolean current;

    @Column(name = "gcm_token")
    public String gcmToken;

    @Column(name = "enable_notification", defaultValue = "true")
    public boolean enableNotification;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.apiCode);
        dest.writeInt(this.active);
        dest.writeByte(current ? (byte) 1 : (byte) 0);
        dest.writeString(this.gcmToken);
        dest.writeByte(enableNotification ? (byte) 1 : (byte) 0);
    }

    public Account() {
    }

    protected Account(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.apiCode = in.readString();
        this.active = in.readInt();
        this.current = in.readByte() != 0;
        this.gcmToken = in.readString();
        this.enableNotification = in.readByte() != 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }
}
