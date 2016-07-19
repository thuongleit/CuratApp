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

    @JsonProperty("user_posted")
    int mPosted;

    @JsonProperty("ig_media_id")
    String mIgMediaId;

    @JsonProperty("ig_media_type")
    String mIgMediaType;

    @JsonProperty("ig_caption_text")
    String mIgCaptionText;

    @JsonProperty("ig_image_low")
    String mIgImageLow;

    @JsonProperty("ig_image_thumbnail")
    String mIgImageThumbnail;

    @JsonProperty("ig_image_standard")
    String mIgImageStandard;

    @JsonProperty("ig_video_low_resolution")
    String mIgVideoLowResolution;

    @JsonProperty("ig_video_standard_resolution")
    String mIgVideoStandardResolution;

    @JsonProperty("media")
    Media mMedia;

    public Post() {
    }

    public Media getMedia() {
        return mMedia;
    }

    public void setMedia(Media mMedia) {
        this.mMedia = mMedia;
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

    public int getPosted() {
        return mPosted;
    }

    public void setPosted(int posted) {
        this.mPosted = posted;
    }

    public String getIgMediaId() {
        return mIgMediaId;
    }

    public void setIgMediaId(String igMediaId) {
        this.mIgMediaId = igMediaId;
    }

    public String getIgMediaType() {
        return mIgMediaType;
    }

    public void setIgMediaType(String igMediaType) {
        this.mIgMediaType = igMediaType;
    }

    public String getIgCaptionText() {
        return mIgCaptionText;
    }

    public void setIgCaptionText(String igCaptionText) {
        this.mIgCaptionText = igCaptionText;
    }

    public String getIgImageLow() {
        return mIgImageLow;
    }

    public void setIgImageLow(String igImageLow) {
        this.mIgImageLow = igImageLow;
    }

    public String getIgImageThumbnail() {
        return mIgImageThumbnail;
    }

    public void setIgImageThumbnail(String igImageThumbnail) {
        this.mIgImageThumbnail = igImageThumbnail;
    }

    public String getIgImageStandard() {
        return mIgImageStandard;
    }

    public void setIgImageStandard(String igImageStandard) {
        this.mIgImageStandard = igImageStandard;
    }

    public String getIgVideoLowResolution() {
        return mIgVideoLowResolution;
    }

    public void setIgVideoLowResolution(String igVideoLowResolution) {
        this.mIgVideoLowResolution = igVideoLowResolution;
    }

    public String getIgVideoStandardResolution() {
        return mIgVideoStandardResolution;
    }

    public void setIgVideoStandardResolution(String igVideoStandardResolution) {
        this.mIgVideoStandardResolution = igVideoStandardResolution;
    }

    public boolean isPosted(){
        return mPosted == 1;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mExecDate);
        dest.writeInt(this.mStatus);
        dest.writeInt(this.mPosted);
        dest.writeString(this.mIgMediaId);
        dest.writeString(this.mIgMediaType);
        dest.writeString(this.mIgCaptionText);
        dest.writeString(this.mIgImageLow);
        dest.writeString(this.mIgImageThumbnail);
        dest.writeString(this.mIgImageStandard);
        dest.writeString(this.mIgVideoLowResolution);
        dest.writeString(this.mIgVideoStandardResolution);
        dest.writeParcelable(this.mMedia, flags);
    }

    protected Post(Parcel in) {
        this.mId = in.readString();
        this.mExecDate = in.readString();
        this.mStatus = in.readInt();
        this.mPosted = in.readInt();
        this.mIgMediaId = in.readString();
        this.mIgMediaType = in.readString();
        this.mIgCaptionText = in.readString();
        this.mIgImageLow = in.readString();
        this.mIgImageThumbnail = in.readString();
        this.mIgImageStandard = in.readString();
        this.mIgVideoLowResolution = in.readString();
        this.mIgVideoStandardResolution = in.readString();
        this.mMedia = in.readParcelable(Media.class.getClassLoader());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
