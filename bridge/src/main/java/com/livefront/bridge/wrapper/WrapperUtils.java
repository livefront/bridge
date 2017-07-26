package com.livefront.bridge.wrapper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Set;

/**
 * Handles the wrapping and unwrapping of certain {@link android.os.Parcelable} objects that use
 * native code to optimize their {@code Parcelable} implementations. When placed into a {@link
 * Bundle} unwrapped, these objects will cause a crash if the {@code Bundle} is written to a
 * {@link android.os.Parcel} that then calls {@link Parcel#marshall()}.
 */
public class WrapperUtils {

    public static void unwrapOptimizedObjects(@NonNull Bundle bundle) {
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            if (bundle.get(key) instanceof BitmapWrapper) {
                BitmapWrapper bitmapWrapper = (BitmapWrapper) bundle.get(key);
                //noinspection ConstantConditions
                bundle.putParcelable(key, bitmapWrapper.getBitmap());
            }
        }
    }

    public static void wrapOptimizedObjects(@NonNull Bundle bundle) {
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            if (bundle.get(key) instanceof Bitmap) {
                Bitmap bitmap = (Bitmap) bundle.get(key);
                //noinspection ConstantConditions
                bundle.putParcelable(key, new BitmapWrapper(bitmap));
            }
        }
    }

}
