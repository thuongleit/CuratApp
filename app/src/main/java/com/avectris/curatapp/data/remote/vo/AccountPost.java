package com.avectris.curatapp.data.remote.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.avectris.curatapp.vo.Post;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
public class AccountPost implements Parcelable {

    @JsonProperty("id")
    String mId;

    @JsonProperty("name")
    String mName;

    @JsonProperty("active")
    int mActive;

    @JsonProperty("client")
    String mClient;

    @JsonProperty("upcoming_posts")
    List<Post> mPosts;

    public AccountPost() {
    }

    private AccountPost(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mActive = in.readInt();
        this.mClient = in.readString();
        in.readTypedList(mPosts, Post.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeInt(this.mActive);
        dest.writeString(this.mClient);
        dest.writeTypedList(mPosts);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getActive() {
        return mActive;
    }

    public void setActive(int active) {
        this.mActive = active;
    }

    public String getClient() {
        return mClient;
    }

    public void setClient(String client) {
        this.mClient = client;
    }

    public List<Post> getPosts() {
        return mPosts;
    }

    public void setPosts(List<Post> posts) {
        this.mPosts = posts;
    }

    public static final Creator<AccountPost> CREATOR = new Creator<AccountPost>() {
        public AccountPost createFromParcel(Parcel source) {
            return new AccountPost(source);
        }

        public AccountPost[] newArray(int size) {
            return new AccountPost[size];
        }
    };
}
