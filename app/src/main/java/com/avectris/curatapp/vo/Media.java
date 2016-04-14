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

    protected Media(Parcel in) {
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

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        if (mId != null ? !mId.equals(media.mId) : media.mId != null) return false;
        if (mFileName != null ? !mFileName.equals(media.mFileName) : media.mFileName != null)
            return false;
        if (mCaptionText != null ? !mCaptionText.equals(media.mCaptionText) : media.mCaptionText != null)
            return false;
        if (mOriginLink != null ? !mOriginLink.equals(media.mOriginLink) : media.mOriginLink != null)
            return false;
        if (mOriginMedia != null ? !mOriginMedia.equals(media.mOriginMedia) : media.mOriginMedia != null)
            return false;
        return mOriginThumb != null ? mOriginThumb.equals(media.mOriginThumb) : media.mOriginThumb == null;

    }
}
