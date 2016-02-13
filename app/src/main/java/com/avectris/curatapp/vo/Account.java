package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class Account implements Parcelable {
    @JsonProperty("id")
    long mId;
    
    @JsonProperty("name")
    String mName;
    
    @JsonProperty("api_code")
    String mApiCode;
    
    @JsonProperty("created_at")
    Date mCreatedAt;
    
    @JsonProperty("updated_at")
    Date mUpdatedAt;
    
    @JsonProperty("client")
    Client mClient;
    
    @JsonProperty("agency")
    Agency mAgency;

    public Account() {
    }

    private Account(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mApiCode = in.readString();
        this.mCreatedAt = in.readParcelable(Date.class.getClassLoader());
        this.mUpdatedAt = in.readParcelable(Date.class.getClassLoader());
        this.mClient = in.readParcelable(Client.class.getClassLoader());
        this.mAgency = in.readParcelable(Agency.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mApiCode);
        dest.writeParcelable(this.mCreatedAt, 0);
        dest.writeParcelable(this.mUpdatedAt, 0);
        dest.writeParcelable(this.mClient, 0);
        dest.writeParcelable(this.mAgency, 0);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getApiCode() {
        return mApiCode;
    }

    public void setApiCode(String apiCode) {
        this.mApiCode = apiCode;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.mCreatedAt = createdAt;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

    public Client getClient() {
        return mClient;
    }

    public void setClient(Client client) {
        this.mClient = client;
    }

    public Agency getAgency() {
        return mAgency;
    }

    public void setAgency(Agency agency) {
        this.mAgency = agency;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
