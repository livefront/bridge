package com.livefront.bridge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.livefront.bridge.disk.FileDiskHandler;
import com.livefront.bridge.helper.Data;
import com.livefront.bridge.helper.SampleTarget;
import com.livefront.bridge.helper.Saveable;
import com.livefront.bridge.util.BundleUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BridgeTest {

    private final Context mContext = InstrumentationRegistry.getInstrumentation().getContext();
    private final ExecutorService mExecutor = Executors.newCachedThreadPool();

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
    public void clearAll_withDataPersistedFromCurrentSession() {
        // Should clear any previously-stored data from disk and memory.

        // Populate the data using a direct call to Bridge.saveInstanceState.
        Data data = new Data("Text");
        SampleTarget initialTarget = new SampleTarget(data);
        Bundle initialBundle = new Bundle();
        Bridge.saveInstanceState(initialTarget, initialBundle);

        // Confirm there is data stored to disk
        String uuidKey = String.format("uuid_%s", initialTarget.getClass().getName());
        String uuid = initialBundle.getString(uuidKey);
        assertNotNull(uuid);
        assertNotNull(buildFileDiskHandler().getBytes(uuid));

        Bridge.clearAll(mContext);

        // Wait for any clearing to happen on a background thread.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // no-op
        }

        // Confirm there is now no data stored to disk.
        assertNull(buildFileDiskHandler().getBytes(uuid));

        SampleTarget finalTarget = new SampleTarget(null);
        assertNull(finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);

        // Confirm data can not be restored from memory
        Bridge.restoreInstanceState(finalTarget, initialBundle);
        assertNull(finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);
    }

    @Test
    public void clearAll_withDataPersistedFromPreviousSession() {
        // Should clear any previously-stored data from disk

        // Populate the data indirectly using a FileDiskHandler to avoid creating a static
        // BridgeDelegate instance.
        String uuid = "bb63d01f-287f-4fd3-b2ed-92a07da40a95";
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        byte[] bytes = BundleUtil.toBytes(bundle);
        buildFileDiskHandler().putBytes(uuid, bytes);
        assertNotNull(buildFileDiskHandler().getBytes(uuid));

        Bridge.clearAll(mContext);

        // Wait for any clearing to happen on a background thread.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // no-op
        }

        // Confirm there is now no data stored to disk.
        assertNull(buildFileDiskHandler().getBytes(uuid));
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

    //region Private helper methods
    @NonNull
    private FileDiskHandler buildFileDiskHandler() {
        return new FileDiskHandler(mContext, mExecutor);
    }
    //endregion Private helper methods
}
