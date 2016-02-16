package com.avectris.curatapp.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/13/16.
 */
public class Media implements Parcelable {
    @JsonProperty("id")
    String mId;

    @JsonProperty("user_id")
    long mUserId;

    @JsonProperty("filename")
    String mFileName;

    @JsonProperty("caption_text")
    String mCaptionText;

    @JsonProperty("origin_link")
    String mOriginLink;

    @JsonProperty("origin_username")
    String mOriginUsername;

    @JsonProperty("origin_media")
    String mOriginMedia;

    @JsonProperty("origin_thumb")
    String mOriginThumb;

    @JsonProperty("created_at")
    Date mCreatedAt;

    @JsonProperty("updated_at")
    Date mUpdatedAt;

    public Media() {
    }

    protected Media(Parcel in) {
        this.mId = in.readString();
        this.mUserId = in.readLong();
        this.mFileName = in.readString();
        this.mCaptionText = in.readString();
        this.mOriginLink = in.readString();
        this.mOriginUsername = in.readString();
        this.mOriginMedia = in.readString();
        this.mOriginThumb = in.readString();
        this.mCreatedAt = in.readParcelable(Date.class.getClassLoader());
        this.mUpdatedAt = in.readParcelable(Date.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeLong(this.mUserId);
        dest.writeString(this.mFileName);
        dest.writeString(this.mCaptionText);
        dest.writeString(this.mOriginLink);
        dest.writeString(this.mOriginUsername);
        dest.writeString(this.mOriginMedia);
        dest.writeString(this.mOriginThumb);
        dest.writeParcelable(this.mCreatedAt, 0);
        dest.writeParcelable(this.mUpdatedAt, 0);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getCaptionText() {
        return mCaptionText;
    }

    public void setCaptionText(String captionText) {
        this.mCaptionText = captionText;
    }

    public String getOriginLink() {
        return mOriginLink;
    }

    public void setOriginLink(String originLink) {
        this.mOriginLink = originLink;
    }

    public String getOriginUsername() {
        return mOriginUsername;
    }

    public void setOriginUsername(String originUsername) {
        this.mOriginUsername = originUsername;
    }

    public String getOriginMedia() {
        return (mOriginMedia != null) ? mOriginMedia.substring(mOriginMedia.indexOf("http"), mOriginMedia.length()) : null;
    }

    public void setOriginMedia(String originMedia) {
        this.mOriginMedia = originMedia;
    }

    public String getOriginThumb() {
        return mOriginThumb;
    }

    public void setOriginThumb(String originThumb) {
        this.mOriginThumb = originThumb;
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

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
