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

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BridgeDelegateTest {

    private final Context mContext = InstrumentationRegistry.getInstrumentation().getContext();
    private final ExecutorService mExecutor = Executors.newCachedThreadPool();
    private final FileDiskHandler mFileDiskHandler = new FileDiskHandler(mContext, mExecutor);
    private final SavedStateHandler mSavedStateHandler = new SavedStateHandler() {
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
    };
    private final BridgeDelegate mDelegate = buildBridgeDelegate();

    @Test
    public void restoreInstanceState_differentInstance_malformedData() {
        // Should not restore the data to the final target and should not throw an exception.
        Data data = new Data("Text");
        SampleTarget initialTarget = new SampleTarget(data);
        Bundle initialBundle = new Bundle();

        mDelegate.saveInstanceState(initialTarget, initialBundle);

        // Overwrite the data with malformed data
        String uuidKey = String.format("uuid_%s", initialTarget.getClass().getName());
        String uuid = initialBundle.getString(uuidKey);
        byte[] malformedBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        mFileDiskHandler.putBytes(uuid, malformedBytes);

        // Originally, a new empty target does not contain the same data as the original target.
        SampleTarget finalTarget = new SampleTarget(null);
        assertNotEquals(data, finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);

        BridgeDelegate newDelegate = buildBridgeDelegate();
        newDelegate.restoreInstanceState(finalTarget, initialBundle);

        // After restoring, the final target still has no data and is not equal to the initial
        // target
        assertNull(finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);
    }

    @Test
    public void restoreInstanceState_differentInstance_truncatedData() {
        // Should not restore the data to the final target and should not throw an exception.
        Data data = new Data("Text");
        SampleTarget initialTarget = new SampleTarget(data);
        Bundle initialBundle = new Bundle();

        mDelegate.saveInstanceState(initialTarget, initialBundle);

        // Get the data written to disk
        String uuidKey = String.format("uuid_%s", initialTarget.getClass().getName());
        String uuid = initialBundle.getString(uuidKey);
        byte[] originalBytes = mFileDiskHandler.getBytes(uuid);
        byte[] truncatedBytes = new byte[originalBytes.length - 4];

        // Overwrite the data with a truncated copy
        System.arraycopy(originalBytes, 0, truncatedBytes, 0, truncatedBytes.length);
        mFileDiskHandler.putBytes(uuid, truncatedBytes);

        // Originally, a new empty target does not contain the same data as the original target.
        SampleTarget finalTarget = new SampleTarget(null);
        assertNotEquals(data, finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);

        BridgeDelegate newDelegate = buildBridgeDelegate();
        newDelegate.restoreInstanceState(finalTarget, initialBundle);

        // After restoring, the final target still has no data and is not equal to the initial
        // target
        assertNull(finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);
    }

    @Test
    public void restoreInstanceState_differentInstance_validData() {
        // Should restore data by loading it from disk
        Data data = new Data("Text");
        SampleTarget initialTarget = new SampleTarget(data);
        Bundle initialBundle = new Bundle();

        mDelegate.saveInstanceState(initialTarget, initialBundle);

        // Check that data is now persisted to disk
        String uuidKey = String.format("uuid_%s", initialTarget.getClass().getName());
        String uuid = initialBundle.getString(uuidKey);
        assertNotNull(mFileDiskHandler.getBytes(uuid));

        // Originally, a new empty target does not contain the same data as the original target.
        SampleTarget finalTarget = new SampleTarget(null);
        assertNotEquals(data, finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);

        BridgeDelegate newDelegate = buildBridgeDelegate();
        newDelegate.restoreInstanceState(finalTarget, initialBundle);

        // After restoring, the final target is the same as the original one.
        assertEquals(data, finalTarget.getData());
        assertEquals(initialTarget, finalTarget);
    }

    @Test
    public void restoreInstanceState_sameInstance() {
        // Should restore data by loading it from memory
        Data data = new Data("Text");
        SampleTarget initialTarget = new SampleTarget(data);
        Bundle initialBundle = new Bundle();

        mDelegate.saveInstanceState(initialTarget, initialBundle);

        // Clear all data to prove we're getting it from memory
        mFileDiskHandler.clearAll();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // no-op
        }

        // Originally, a new empty target does not contain the same data as the original target.
        SampleTarget finalTarget = new SampleTarget(null);
        assertNotEquals(data, finalTarget.getData());
        assertNotEquals(initialTarget, finalTarget);

        mDelegate.restoreInstanceState(finalTarget, initialBundle);

        // After restoring, the final target is the same as the original one.
        assertEquals(data, finalTarget.getData());
        assertEquals(initialTarget, finalTarget);
    }

    //region Private helper methods
    @NonNull
    private BridgeDelegate buildBridgeDelegate() {
        return new BridgeDelegate(
                mContext,
                Executors.newSingleThreadExecutor(),
                mSavedStateHandler,
                null);
    }
    //endregion Private helper methods
}
