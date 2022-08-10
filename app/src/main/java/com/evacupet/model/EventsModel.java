package com.evacupet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

public class EventsModel implements Parcelable {
        private ParseObject parseObject;
        private boolean isChecked;

        public EventsModel(ParseObject object1) {
                this.parseObject = object1;
        }

        protected EventsModel(Parcel in) {
                parseObject = in.readParcelable(ParseObject.class.getClassLoader());
                isChecked = in.readByte() != 0;
        }
        public static final Creator<EventsModel> CREATOR = new Creator<EventsModel>() {
                @Override
                public EventsModel createFromParcel(Parcel in) {
                        return new EventsModel(in);
                }

                @Override
                public EventsModel[] newArray(int size) {
                        return new EventsModel[size];
                }
        };

        @Override
        public int describeContents() {
                return 0;
        }
        public ParseObject getParseObject() {return parseObject;}
        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeParcelable(parseObject, flags);
                dest.writeByte((byte) (isChecked ? 1 : 0));
        }
}
