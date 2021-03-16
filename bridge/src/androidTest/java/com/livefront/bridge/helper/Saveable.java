package com.livefront.bridge.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Simple interface that can be implemented by test classes and checked inside a
 * {@link com.livefront.bridge.SavedStateHandler} rather than using something like Icepick.
 */
public interface Saveable {
    void saveState(@NonNull Bundle bundle);

    void restoreState(@Nullable Bundle bundle);
}
