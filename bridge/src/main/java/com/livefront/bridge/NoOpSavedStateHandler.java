package com.livefront.bridge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class NoOpSavedStateHandler implements SavedStateHandler {

    @Override
    public void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
        // no-op
    }

    @Override
    public void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
        // no-op
    }

}
