package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/13/16.
 */
public class Post implements Parcelable{
    @JsonProperty("id")
    String mId;
    @JsonProperty("exec_datetime")
    String mExecuteTime;
    @JsonProperty("status")
    int mStatus;
    @JsonProperty("media")
    Media mMedia;

    public Post() {
    }

    private Post(Parcel in) {
        this.mId = in.readString();
        this.mExecuteTime = in.readString();
        this.mStatus = in.readInt();
        this.mMedia = in.readParcelable(Media.class.getClassLoader());
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mExecuteTime);
        dest.writeInt(this.mStatus);
        dest.writeParcelable(this.mMedia, 0);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getExecuteTime() {
        return mExecuteTime;
    }

    public void setExecuteTime(String executeTime) {
        this.mExecuteTime = executeTime;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public Media getMedia() {
        return mMedia;
    }

    public void setMedia(Media media) {
        this.mMedia = media;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
