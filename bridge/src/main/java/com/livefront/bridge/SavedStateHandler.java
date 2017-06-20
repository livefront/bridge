package com.livefront.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface SavedStateHandler {

    void saveInstanceState(@NonNull Object target, @NonNull Bundle state);

    void restoreInstanceState(@NonNull Object target, @Nullable Bundle state);

}
