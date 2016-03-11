package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/13/16.
 */
public class Post implements Parcelable {
    @JsonProperty("id")
    String mId;

    @JsonProperty("exec_datetime")
    String mExecDate;

    @JsonProperty("status")
    int mStatus;

    @JsonProperty("media")
    Media mMedia;

    public Post() {
    }

    protected Post(Parcel in) {
        this.mId = in.readString();
        this.mExecDate = in.readString();
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
        dest.writeString(this.mExecDate);
        dest.writeInt(this.mStatus);
        dest.writeParcelable(this.mMedia, 0);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }


    public String getExecDate() {
        return mExecDate;
    }

    public void setExecDate(String execDate) {
        this.mExecDate = execDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (mStatus != post.mStatus) return false;
        if (mId != null ? !mId.equals(post.mId) : post.mId != null) return false;
        if (mExecDate != null ? !mExecDate.equals(post.mExecDate) : post.mExecDate != null)
            return false;
        return mMedia != null ? mMedia.equals(post.mMedia) : post.mMedia == null;

    }
}
