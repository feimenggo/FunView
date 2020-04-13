package com.feimeng.imageviewer.config;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ViewerConfig implements Parcelable {
    public static int PHOTO = 1;
    public static int VIDEO = 2;
    private int type = PHOTO;
    private String[] imageUrls;
    private List<ContentViewOriginModel> contentViewOriginModels;
    private int position;
    private int headerSize;

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<ContentViewOriginModel> getContentViewOriginModels() {
        return contentViewOriginModels;
    }

    public void setContentViewOriginModels(List<ContentViewOriginModel> contentViewOriginModels) {
        this.contentViewOriginModels = contentViewOriginModels;
    }

    public ViewerConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeStringArray(this.imageUrls);
        dest.writeTypedList(this.contentViewOriginModels);
        dest.writeInt(this.position);
        dest.writeInt(this.headerSize);
    }

    protected ViewerConfig(Parcel in) {
        this.type = in.readInt();
        this.imageUrls = in.createStringArray();
        this.contentViewOriginModels = in.createTypedArrayList(ContentViewOriginModel.CREATOR);
        this.position = in.readInt();
        this.headerSize = in.readInt();
    }

    public static final Creator<ViewerConfig> CREATOR = new Creator<ViewerConfig>() {
        @Override
        public ViewerConfig createFromParcel(Parcel source) {
            return new ViewerConfig(source);
        }

        @Override
        public ViewerConfig[] newArray(int size) {
            return new ViewerConfig[size];
        }
    };
}
