package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.avectris.curatapp.data.local.CuratAppDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonProperty("push_notification")
    @Column(name = "enable_notification", defaultValue = "0")
    public int enableNotification;

    @Column
    @JsonIgnore
    public String userEmail;

    public Account() {
    }

    @Override
    public String toString() {
        return name;
    }

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
        dest.writeInt(this.enableNotification);
        dest.writeString(this.userEmail);
    }

    protected Account(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.apiCode = in.readString();
        this.active = in.readInt();
        this.current = in.readByte() != 0;
        this.gcmToken = in.readString();
        this.enableNotification = in.readInt();
        this.userEmail = in.readString();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return id != null ? id.equals(account.id) : account.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isEnableNotification() {
        return enableNotification == 1;
    }
}
