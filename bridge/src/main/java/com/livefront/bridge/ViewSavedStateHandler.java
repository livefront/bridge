package com.livefront.bridge;

import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A handler specifically for saving and restoring the state of {@link android.view.View} objects.
 */
public interface ViewSavedStateHandler {
    @NonNull
    <T extends View> Parcelable saveInstanceState(@NonNull T target,
                                                  @Nullable Parcelable parentState);

    @Nullable
    <T extends View> Parcelable restoreInstanceState(@NonNull T target,
                                                     @Nullable Parcelable state);

}
