package com.livefront.bridge.disk;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileDiskHandlerTest {
    private final Context mContext = InstrumentationRegistry.getInstrumentation().getContext();
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private FileDiskHandler mFileDiskHandler;

    @Before
    public void setUp() {
        // Start each test with a fresh set of files
        deleteAllFiles(mContext);

        mFileDiskHandler = new FileDiskHandler(mContext, mExecutorService);
    }

    @Test
    public void clear_nonMatchingKey() {
        // Should not clear data that does not match the key

        // Confirm data exists before clear
        String key = "test";
        byte[] bytes = new byte[]{1, 2, 3};
        mFileDiskHandler.putBytes(key, bytes);

        assertArrayEquals(bytes, mFileDiskHandler.getBytes(key));

        mFileDiskHandler.clear("other-key");

        assertArrayEquals(bytes, mFileDiskHandler.getBytes(key));
    }

    @Test
    public void clear_matchingKey() {
        // Should clear data previously stored for the given key

        // Confirm data exists before clear
        String key = "test";
        byte[] bytes = new byte[]{1, 2, 3};
        mFileDiskHandler.putBytes(key, bytes);

        assertArrayEquals(bytes, mFileDiskHandler.getBytes(key));

        mFileDiskHandler.clear(key);

        assertNull(mFileDiskHandler.getBytes(key));
    }

    @Test
    public void clearAll() {
        // Should clear all previously saved data

        // Confirm data exists before clear
        String key = "test";
        byte[] bytes = new byte[]{1, 2, 3};
        mFileDiskHandler.putBytes(key, bytes);

        assertArrayEquals(bytes, mFileDiskHandler.getBytes(key));

        mFileDiskHandler.clearAll();

        assertNull(mFileDiskHandler.getBytes(key));
    }

    @Test
    public void constructor() {
        // Should load existing files into memory on a background thread for quick access later

        // Store some original data
        String key = "test";
        byte[] bytes = new byte[]{1, 2, 3};
        mFileDiskHandler.putBytes(key, bytes);

        // Create new handler to show that the data supplied to a previous handler is typically
        // available here.
        FileDiskHandler fileDiskHandler1 = new FileDiskHandler(mContext, mExecutorService);
        assertArrayEquals(bytes, fileDiskHandler1.getBytes(key));

        // Create new handler to show that data is loaded into memory on startup
        FileDiskHandler fileDiskHandler2 = new FileDiskHandler(mContext, mExecutorService);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // no-op
        }

        // Clear all files manually to prove data exists in memory due to startup load
        deleteAllFiles(mContext);

        // Check the data is still stored in memory
        assertArrayEquals(bytes, fileDiskHandler2.getBytes(key));

        // Create new handler to confirm the data has been deleted from disk
        FileDiskHandler fileDiskHandler3 = new FileDiskHandler(mContext, mExecutorService);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // no-op
        }

        assertNull(fileDiskHandler3.getBytes(key));
    }

    @Test
    public void getBytes_afterCancelling() {
        // Should not trigger a CancellationException
        mFileDiskHandler.clearAll();
        mFileDiskHandler.getBytes("test");
    }

    @Test
    public void getBytes_dataPresent() {
        // Should return the previously stored data
        String key = "test";
        byte[] bytes = new byte[]{1, 2, 3};
        mFileDiskHandler.putBytes(key, bytes);

        assertArrayEquals(bytes, mFileDiskHandler.getBytes(key));
    }

    @Test
    public void getBytes_dataNotPresent() {
        // Should return null
        String key = "test";
        assertNull(mFileDiskHandler.getBytes(key));
    }

    @Test
    public void putBytes() {
        // Should write over any existing data
        String key = "test";
        byte[] oldBytes = new byte[]{1, 2, 3};
        mFileDiskHandler.putBytes(key, oldBytes);

        byte[] newBytes = new byte[]{4, 5, 6};
        mFileDiskHandler.putBytes(key, newBytes);

        assertArrayEquals(newBytes, mFileDiskHandler.getBytes(key));
    }

    private void deleteAllFiles(@NonNull Context context) {
        File data = context.getDir("com.livefront.bridge", Context.MODE_PRIVATE);
        File[] files = data.listFiles();
        if (files != null) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }
}
