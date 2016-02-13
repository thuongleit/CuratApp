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
    @JsonProperty("filename")
    String mFileName;
    @JsonProperty("caption_text")
    String mCaptionText;
    @JsonProperty("origin_link")
    String mOriginLink;
    @JsonProperty("origin_media")
    String mOriginMedia;
    @JsonProperty("origin_thumb")
    String mOriginThumb;

    public Media() {
    }

    private Media(Parcel in) {
        this.mId = in.readString();
        this.mFileName = in.readString();
        this.mCaptionText = in.readString();
        this.mOriginLink = in.readString();
        this.mOriginMedia = in.readString();
        this.mOriginThumb = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mFileName);
        dest.writeString(this.mCaptionText);
        dest.writeString(this.mOriginLink);
        dest.writeString(this.mOriginMedia);
        dest.writeString(this.mOriginThumb);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
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

    public String getOriginMedia() {
        return mOriginMedia;
    }

    public void setOriginMedia(String originMedia) {
        this.mOriginMedia = originMedia;
    }

    public String getOrginThumb() {
        return mOriginThumb;
    }

    public void setOrginThumb(String originThumb) {
        this.mOriginThumb = originThumb;
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
