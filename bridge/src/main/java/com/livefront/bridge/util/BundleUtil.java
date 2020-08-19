package com.livefront.bridge.util;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Base64;
import androidx.annotation.NonNull;

/** Helper class for converting {@link Bundle} instances to and from bytes and encoded Strings. */
public class BundleUtil {
  /**
   * Converts the given {@link Bundle} to raw bytes.
   *
   * <p>Note that if the {@code Bundle} contains some highly specialized classes {@link
   * android.os.IBinder}, this process will fail.
   *
   * @param bundle The {@code Bundle} to convert.
   * @return The {@code Bundle} as bytes.
   */
  public static byte[] toBytes(@NonNull Bundle bundle) {
    Parcel parcel = Parcel.obtain();
    parcel.writeBundle(bundle);
    byte[] bytes = parcel.marshall();
    parcel.recycle();
    return bytes;
  }

  /**
   * Converts the given {@link Bundle} to a Base64 encoded String.
   *
   * <p>Note that if the {@code Bundle} contains some highly specialized classes {@link
   * android.os.IBinder}, this process will fail.
   *
   * @param bundle The {@code Bundle} to convert.
   * @return The {@code Bundle} as a Base64 encoded String.
   */
  @NonNull
  public static String toEncodedString(@NonNull Bundle bundle) {
    return Base64.encodeToString(toBytes(bundle), 0);
  }

  /**
   * Converts the given bytes to a {@link Bundle}.
   *
   * <p>Note that if the bytes do not represent a {@code Bundle} that was previously converted with
   * {@link #toBytes(Bundle)}, this process will likely fail.
   *
   * @param bytes The bytes to convert.
   * @return The resulting {@code Bundle}.
   */
  @NonNull
  public static Bundle fromBytes(byte[] bytes) {
    Parcel parcel = Parcel.obtain();
    parcel.unmarshall(bytes, 0, bytes.length);
    parcel.setDataPosition(0);
    Bundle bundle;
    bundle = parcel.readBundle(BundleUtil.class.getClassLoader());
    if (bundle == null) bundle = new Bundle();
    parcel.recycle();
    return bundle;
  }

  /**
   * Converts the given Base64 encoded String to a {@link Bundle}.
   *
   * <p>Note that if the String does not represent a {@code Bundle} that was previously converted
   * with {@link #toEncodedString(Bundle)} (Bundle)}, this process will likely fail.
   *
   * @param encodedString The bytes to convert.
   * @return The resulting {@code Bundle}.
   */
  @NonNull
  public static Bundle fromEncodedString(@NonNull String encodedString) {
    return fromBytes(Base64.decode(encodedString, 0));
  }
}
