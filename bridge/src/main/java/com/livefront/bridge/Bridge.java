package com.livefront.bridge;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Bridge {

    private static volatile BridgeDelegate sDelegate;

    private static synchronized void checkInitialization() {
        if (sDelegate == null) {
            throw new IllegalStateException(
                    "You must first call initialize before calling any other methods");
        }
    }

    /**
     * Clears any data associated with the given target object that may be stored to disk. This
     * will not affect data stored for restoration after configuration changes. Due to how these
     * changes are monitored, this method will have no affect prior to
     * {@link VERSION_CODES#ICE_CREAM_SANDWICH}.
     * <p>
     * It is required to call {@link #initialize(Context, SavedStateHandler)} before calling this
     * method.
     */
    public static void clear(@NonNull Object target) {
        checkInitialization();
        sDelegate.clear(target);
    }

    /**
     * Clears all data from disk and memory. Does not require a call to {@link #initialize(Context,
     * SavedStateHandler)}.
     */
    public static void clearAll(@NonNull Context context) {
        BridgeDelegate delegate = sDelegate != null
                ? sDelegate
                : new BridgeDelegate(context, new NoOpSavedStateHandler(), null);
        delegate.clearAll();
    }

    /**
     * Initializes the framework used to save and restore data and route it to a location free from
     * {@link android.os.TransactionTooLargeException}. The actual state saving and restoration
     * of each object will be performed by the provided {@link SavedStateHandler}. Note that in
     * order to save the state of {@link View} objects, you must instead use
     * {@link #initialize(Context, SavedStateHandler, ViewSavedStateHandler)}.
     *
     * @param context           an application {@link Context} necessary for saving state to disk
     * @param savedStateHandler used to do the actual state saving and restoring for a given object
     */
    public static void initialize(@NonNull Context context,
                                  @NonNull SavedStateHandler savedStateHandler) {
        initializeInternal(context, savedStateHandler, null);
    }

    /**
     * Initializes the framework used to save and restore data and route it to a location free from
     * {@link android.os.TransactionTooLargeException}. The actual state saving and restoration
     * of each object will be performed by the provided {@link SavedStateHandler} for
     * non-{@link View} objects and {@link ViewSavedStateHandler} for {@code View} objects
     * (when set).
     *
     * @param context               an application {@link Context} necessary for saving state to disk
     * @param savedStateHandler     used to do the actual state saving and restoring for a given
     *                              object
     * @param viewSavedStateHandler used to do the actual state saving and restoring for a given
     *                              {@code View}
     */
    public static void initialize(@NonNull Context context,
                                  @NonNull SavedStateHandler savedStateHandler,
                                  @NonNull ViewSavedStateHandler viewSavedStateHandler) {
        initializeInternal(context, savedStateHandler, viewSavedStateHandler);
    }

    private static synchronized void initializeInternal(
            @NonNull Context context,
            @NonNull SavedStateHandler savedStateHandler,
            @Nullable ViewSavedStateHandler viewSavedStateHandler) {
        sDelegate = new BridgeDelegate(context, savedStateHandler, viewSavedStateHandler);
    }

    /**
     * Restores the state of the given target object based on tracking information stored in the
     * given {@link Bundle}. The actual saved data will be retrieved from a location in memory or
     * stored on disk.
     * <p>
     * It is required to call {@link #initialize(Context, SavedStateHandler)} before calling this
     * method.
     */
    public static void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
        checkInitialization();
        sDelegate.restoreInstanceState(target, state);
    }

    /**
     * Restores the state of the given {@link View} based on tracking information stored in the
     * given {@link Parcelable} object. The actual saved data will be retrieved from a location in
     * memory or stored on disk.
     * <p>
     * It is required to call {@link #initialize(Context, SavedStateHandler, ViewSavedStateHandler)}
     * before calling this method.
     *
     * @return The saved state of the parent {@code View}. This is intended to be passed to the
     * super method call inside a View's {@code onRestoreInstanceState}.
     */
    @Nullable
    public static <T extends View> Parcelable restoreInstanceState(@NonNull T target,
                                                                   @Nullable Parcelable state) {
        checkInitialization();
        return sDelegate.restoreInstanceState(target, state);
    }

    /**
     * Saves the state of the given target object to a location in memory and disk and stores
     * tracking information in given {@link Bundle}.
     * <p>
     * It is required to call {@link #initialize(Context, SavedStateHandler)} before calling this
     * method.
     */
    public static void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
        checkInitialization();
        sDelegate.saveInstanceState(target, state);
    }

    /**
     * Saves the state of the given {@link View} to a location in memory and disk and stores
     * tracking information in the returned {@link Parcelable} object.
     * <p>
     * It is required to call {@link #initialize(Context, SavedStateHandler)} before calling this
     * method.
     *
     * @return The saved state of the {@code View}, to be returned in its
     * {@code onSaveInstanceState} method.
     */
    @NonNull
    public static <T extends View> Parcelable saveInstanceState(@NonNull T target,
                                                                @Nullable Parcelable parentState) {
        checkInitialization();
        return sDelegate.saveInstanceState(target, parentState);
    }

}
