package com.livefront.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A handler for generic, non-{@link android.view.View} objects. To save the state of Views, see
 * {@link ViewSavedStateHandler}.
 */
public interface SavedStateHandler {

    void saveInstanceState(@NonNull Object target, @NonNull Bundle state);

    void restoreInstanceState(@NonNull Object target, @Nullable Bundle state);

}
