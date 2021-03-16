package com.livefront.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.livefront.bridge.helper.Data;
import com.livefront.bridge.helper.SampleTarget;
import com.livefront.bridge.helper.Saveable;

import org.junit.Before;
import org.junit.Test;

public class BridgeTest {
    @Before
    public void setUp() {
        Bridge.initialize(
                InstrumentationRegistry.getInstrumentation().getContext(),
                new SavedStateHandler() {
                    @Override
                    public void saveInstanceState(@NonNull Object target,
                                                  @NonNull Bundle state) {
                        ((Saveable) target).saveState(state);
                    }

                    @Override
                    public void restoreInstanceState(@NonNull Object target,
                                                     @Nullable Bundle state) {
                        ((Saveable) target).restoreState(state);
                    }
                });
    }

    @Test
    public void saveInstanceState() {
        // Should place a single UUID into the passed-in Bundle. When restoreInstanceState is
        // called with a new object using that Bundle, the original data should be restored.
        // Attempting to restore data to an additional target should fail, as the data is cleared
        // after the first restore call.
        Data data = new Data("Text");
        SampleTarget initialTarget = new SampleTarget(data);
        Bundle initialBundle = new Bundle();

        Bridge.saveInstanceState(initialTarget, initialBundle);

        // The Bundle now only contains special Bridge-supplied the UUID key
        String uuidKey = String.format("uuid_%s", initialTarget.getClass().getName());
        assertTrue(initialBundle.containsKey(uuidKey));
        assertEquals(1, initialBundle.keySet().size());

        // Originally, a new empty target does not contain the same data as the original target.
        SampleTarget finalTarget = new SampleTarget(null);
        assertNotEquals(data, finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);

        Bridge.restoreInstanceState(finalTarget, initialBundle);

        // After restoring with Bridge, the final target is the same as the original one
        assertEquals(data, finalTarget.getData());
        assertEquals(initialTarget, finalTarget);

        // Wait for any clearing to happen on a background thread.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // no-op
        }

        // Attempting to restore one more time will result in a failure to update the object, as
        // the stored data is deleted after the first call.
        SampleTarget additionalTarget = new SampleTarget(null);

        Bridge.restoreInstanceState(additionalTarget, initialBundle);

        assertNotEquals(data, additionalTarget.getData());
        assertNotEquals(initialTarget, additionalTarget);
    }
}
