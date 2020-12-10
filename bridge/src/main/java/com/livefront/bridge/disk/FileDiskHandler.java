package com.livefront.bridge.disk;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple implementation of {@link DiskHandler} that saves the requested data to individual files.
 * Similar to {@link android.content.SharedPreferences}, this implementation will begin loading the
 * saved data into memory as soon as it is constructed and block on the first call until all data is
 * loaded (or some cutoff is reached).
 */
public class FileDiskHandler implements DiskHandler {

  private static final String DIRECTORY_NAME = "com.livefront.bridge";

  /**
   * Time to wait (in milliseconds) while loading the initial data in the background. After this
   * timeout data will continue to load in the background but some calls to {@link
   * FileDiskHandler#getBytes(String)} may then block on-demand.
   */
  private static final long BACKGROUND_WAIT_TIMEOUT_MS = 1000;

  private final File mDirectory;
  private final Future<?> mPendingLoadFuture;
  private final Map<String, byte[]> mKeyByteMap = new ConcurrentHashMap<>();

  /**
   * Determines whether the {@link #waitForFilesToLoad()} call has completed in some way.
   */
  private volatile boolean mIsLoadedOrTimedOut = false;

  /**
   * Creates the handler and begins loading its data into memory on a background thread.
   *
   * @param context         The {@link Context} used to derive the file-storage location.
   * @param executorService An {@link ExecutorService} that can be used to place the initial data
   *                        loading on a background thread.
   */
  public FileDiskHandler(@NonNull Context context, @NonNull ExecutorService executorService) {
    mDirectory = context.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);

    // Load all the files into memory in the background.
    mPendingLoadFuture = executorService.submit(this::loadAllFiles);
  }

  @Override
  public void clearAll() {
    cancelFileLoading();
    mKeyByteMap.clear();
    deleteFilesByKey(null);
  }

  @Override
  public void clear(@NonNull String key) {
    cancelFileLoading();
    mKeyByteMap.remove(key);
    deleteFilesByKey(key);
  }

  @Nullable
  @Override
  public byte[] getBytes(@NonNull String key) {
    waitForFilesToLoad();
    return getBytesInternal(key);
  }

  @Override
  public void putBytes(@NonNull final String key, @NonNull final byte[] bytes) {
    // Place the data in memory first
    mKeyByteMap.put(key, bytes);

    // Write the data to disk
    File file = new File(mDirectory, key);
    FileOutputStream outStream;
    try {
      outStream = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      // If there is a problem with the file we'll simply abort.
      return;
    }
    try {
      outStream.write(bytes);
    } catch (IOException e) {
      // Ignore
    } finally {
      try {
        outStream.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }

  /**
   * Cancels any pending file loading that happens at startup.
   */
  private void cancelFileLoading() {
    mPendingLoadFuture.cancel(true);
  }

  /**
   * Deletes all files associated with the given {@code key}. If the key is {@code null}, then all
   * stored files will be deleted.
   *
   * @param key The key associated with the data to delete (or {@code null} if all data should be
   *            deleted).
   */
  private void deleteFilesByKey(@Nullable String key) {
    File[] files = mDirectory.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      if (key == null || getFileNameForKey(key).equals(file.getName())) {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
      }
    }
  }

  @Nullable
  private byte[] getBytesFromDisk(@NonNull String key) {
    File file = getFileByKey(key);
    if (file == null) {
      return null;
    }

    FileInputStream inputStream;
    try {
      inputStream = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      return null;
    }

    byte[] bytes = new byte[(int) file.length()];
    try {
      //noinspection ResultOfMethodCallIgnored
      inputStream.read(bytes);
    } catch (IOException e) {
      return null;
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        // Ignore
      }
    }
    return bytes;
  }

  @Nullable
  private byte[] getBytesInternal(@NonNull String key) {
    // Check for data loaded into memory from the initial load
    byte[] cachedBytes = mKeyByteMap.get(key);
    if (cachedBytes != null) {
      return cachedBytes;
    }

    // Get bytes from disk and place into memory if necessary
    byte[] bytes = getBytesFromDisk(key);
    if (bytes != null) {
      mKeyByteMap.put(key, bytes);
    }

    return bytes;
  }

  private String getFileNameForKey(@NonNull String key) {
    // For now the key and filename are equivalent
    return key;
  }

  @Nullable
  private File getFileByKey(@NonNull String key) {
    File[] files = mDirectory.listFiles();
    if (files == null) {
      return null;
    }
    for (File file : files) {
      if (getFileNameForKey(key).equals(file.getName())) {
        return file;
      }
    }
    return null;
  }

  private String getKeyForFileName(@NonNull String fileName) {
    // For now the key and filename are equivalent
    return fileName;
  }

  private void loadAllFiles() {
    File[] files = mDirectory.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      // Populate the cached map
      String key = getKeyForFileName(file.getName());
      getBytesInternal(key);
    }
  }

  private void waitForFilesToLoad() {
    if (mIsLoadedOrTimedOut) {
      // No need to wait.
      return;
    }
    try {
      mPendingLoadFuture.get(BACKGROUND_WAIT_TIMEOUT_MS, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      // We've made a best effort to load the data in the background. We can simply proceed
      // here.
    }
    mIsLoadedOrTimedOut = true;
  }
}
