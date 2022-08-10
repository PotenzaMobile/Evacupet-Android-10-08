package com.evacupet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

public class EvenParseMode implements Parcelable {
    private ParseObject parseObject;
    private boolean isChecked;

    protected EvenParseMode(Parcel in) {
        this.parseObject = in.readParcelable(ParseObject.class.getClassLoader());
        this.isChecked = in.readByte() != 0;
    }

    public static final Creator<EvenParseMode> CREATOR = new Creator<EvenParseMode>() {
        @Override
        public EvenParseMode createFromParcel(Parcel in) {
            return new EvenParseMode(in);
        }

        @Override
        public EvenParseMode[] newArray(int size) {
            return new EvenParseMode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
