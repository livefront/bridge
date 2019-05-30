package com.livefront.bridge.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Sample class for demonstrating that {@link Parcelable} data can be safely converted with
 * {@link com.livefront.bridge.Bridge} and {@link com.livefront.bridge.util.BundleUtil}.
 */
public class Data implements Parcelable {

    private String mText;

    public Data(@NonNull String text) {
        mText = text;
    }

    @NonNull
    public String getText() {
        return mText;
    }

    //region Equals and Hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Data data = (Data) o;
        return Objects.equals(mText, data.mText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mText);
    }
    //endregion Equals and Hashcode

    //region Parcelable
    Data(Parcel in) {
        mText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
    //endregion Parcelable
}
