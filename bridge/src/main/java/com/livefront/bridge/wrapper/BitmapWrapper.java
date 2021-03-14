package com.livefront.bridge.wrapper;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

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
        int width = in.readInt();
        int height = in.readInt();
        Bitmap.Config config = Bitmap.Config.values()[in.readInt()];
        byte[] bytes = in.createByteArray();

        mBitmap = Bitmap.createBitmap(width, height, config);
        mBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytes));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        int size;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            size = mBitmap.getAllocationByteCount();
        } else {
            size = mBitmap.getByteCount();
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        mBitmap.copyPixelsToBuffer(byteBuffer);

        dest.writeInt(mBitmap.getWidth());
        dest.writeInt(mBitmap.getHeight());
        dest.writeInt(mBitmap.getConfig().ordinal());
        dest.writeByteArray(byteBuffer.array());
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
