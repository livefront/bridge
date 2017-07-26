package com.livefront.bridge.wrapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;

/**
 * A wrapper class for a {@link Bitmap} that can be placed into a {@link android.os.Bundle} that
 * may be written to disk.
 */
class BitmapWrapper implements Parcelable {

    private Bitmap mBitmap;

    public BitmapWrapper(@NonNull Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    //region Parcelable
    protected BitmapWrapper(Parcel in) {
        byte[] bytes = in.createByteArray();
        mBitmap = BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.length);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        dest.writeByteArray(stream.toByteArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BitmapWrapper> CREATOR = new Creator<BitmapWrapper>() {
        @Override
        public BitmapWrapper createFromParcel(Parcel in) {
            return new BitmapWrapper(in);
        }

        @Override
        public BitmapWrapper[] newArray(int size) {
            return new BitmapWrapper[size];
        }
    };
    //endregion Parcelable

}
