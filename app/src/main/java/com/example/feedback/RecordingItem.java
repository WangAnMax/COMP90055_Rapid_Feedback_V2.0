/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package com.example.feedback;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingItem implements Parcelable {
    private String mName; // file name
    private String mFilePath; //file path
    //private int mId; //id in database
    private int mLength; // length of recording in seconds

    public RecordingItem() {
    }


    public RecordingItem(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
        mLength = in.readInt();
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public static final Creator<RecordingItem> CREATOR = new Creator<RecordingItem>() {
        public RecordingItem createFromParcel(Parcel in) {
            return new RecordingItem(in);
        }

        public RecordingItem[] newArray(int size) {
            return new RecordingItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(mId);
        dest.writeInt(mLength);
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}