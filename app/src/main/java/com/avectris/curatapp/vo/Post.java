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

    @JsonProperty("schedule_id")
    long mScheduleId;

    @JsonProperty("exec_date")
    String mExecDate;

    @JsonProperty("exec_time")
    String mExecTime;

    @JsonProperty("media_id")
    long mMediaId;

    @JsonProperty("status")
    int mStatus;

    @JsonProperty("created_at")
    Date mCreatedAt;

    @JsonProperty("updated_at")
    Date mUpdatedAt;

    @JsonProperty("media")
    Media mMedia;


    public Post() {
    }

    protected Post(Parcel in) {
        this.mId = in.readString();
        this.mScheduleId = in.readLong();
        this.mExecDate = in.readString();
        this.mExecTime = in.readString();
        this.mMediaId = in.readLong();
        this.mStatus = in.readInt();
        this.mCreatedAt = in.readParcelable(Date.class.getClassLoader());
        this.mUpdatedAt = in.readParcelable(Date.class.getClassLoader());
        this.mMedia = in.readParcelable(Media.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeLong(this.mScheduleId);
        dest.writeString(this.mExecDate);
        dest.writeString(this.mExecTime);
        dest.writeLong(this.mMediaId);
        dest.writeInt(this.mStatus);
        dest.writeParcelable(this.mCreatedAt, 0);
        dest.writeParcelable(this.mUpdatedAt, 0);
        dest.writeParcelable(this.mMedia, 0);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public long getScheduleId() {
        return mScheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.mScheduleId = scheduleId;
    }

    public String getExecDate() {
        return mExecDate;
    }

    public void setExecDate(String execDate) {
        this.mExecDate = execDate;
    }

    public String getExecTime() {
        return mExecTime;
    }

    public void setExecTime(String execTime) {
        this.mExecTime = execTime;
    }

    public long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(long mediaId) {
        this.mMediaId = mediaId;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
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
