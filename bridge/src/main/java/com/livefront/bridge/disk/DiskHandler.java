package com.livefront.bridge.disk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A simple interface for managing the storage and retrieval of data to and from disk. All calls
 * should be assumed to be blocking.
 */
public interface DiskHandler {
    /**
     * Clears any saved data associated with the given {@code key}.
     *
     * @param key The key for the save data to clear.
     */
    void clear(@NonNull String key);

    /**
     * Clears all saved data currently stored by the handler.
     */
    void clearAll();

    /**
     * Retrieves the saved data associated with the given {@code key} as a byte array (or
     * {@code null} if there is no data available).
     *
     * @param key The key associated with the saved data to be retrieved.
     * @return The saved data as a byte array (or {@code null} if there is no data available).
     */
    @Nullable
    byte[] getBytes(@NonNull String key);

    /**
     * Stores the given {@code bytes} to disk and associates them with the given {@code key} for
     * retrieval later.
     *
     * @param key   The key to associate with the saved data.
     * @param bytes The data to be saved.
     */
    void putBytes(@NonNull String key, @NonNull byte[] bytes);
}
