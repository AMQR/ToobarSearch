package com.am.toolbarsearch.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.pinyinsearch.model.PinyinSearchUnit;

import java.util.List;

/**
 * User: LJM
 * Date&Time: 2016-10-20 & 21:38
 * Describe: Describe Text
 */
public class JokeEntity {
    public List<ContentlistBean> contentlist;

    public static class ContentlistBean implements Parcelable {
        public String id;
        public String title;
        public String text;
        public int type;
        public String ct;


        protected ContentlistBean(Parcel in) {
            id = in.readString();
            title = in.readString();
            text = in.readString();
            type = in.readInt();
            ct = in.readString();
        }

        public static final Creator<ContentlistBean> CREATOR = new Creator<ContentlistBean>() {
            @Override
            public ContentlistBean createFromParcel(Parcel in) {
                return new ContentlistBean(in);
            }

            @Override
            public ContentlistBean[] newArray(int size) {
                return new ContentlistBean[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(title);
            parcel.writeString(text);
            parcel.writeInt(type);
            parcel.writeString(ct);
        }
    }
}
