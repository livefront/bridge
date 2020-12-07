package com.livefront.bridge.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import com.livefront.bridge.helper.Data;
import org.junit.Test;

public class BundleUtilTest {

  private static final String DATA_KEY = "data";
  private static final String SAMPLE_TEXT = "sample text";

  @Test
  public void toBytes() {
    // Should produce a byte array that can be converted back to a Bundle with the same data
    // using fromBytes
    Data sampleData = new Data(SAMPLE_TEXT);
    Bundle bundle = new Bundle();
    bundle.putParcelable(DATA_KEY, sampleData);

    byte[] bytes = BundleUtil.toBytes(bundle);

    assertTrue(bytes.length > 0);

    Bundle outputBundle = BundleUtil.fromBytes(bytes);
    Data outputData = outputBundle.getParcelable(DATA_KEY);

    assertEquals(sampleData, outputData);
  }

  @Test
  public void toEncodedString() {
    // Should produce a String that can be converted back to a Bundle with the same data using
    // fromEncodedString
    Data sampleData = new Data(SAMPLE_TEXT);
    Bundle bundle = new Bundle();
    bundle.putParcelable(DATA_KEY, sampleData);

    String encodedString = BundleUtil.toEncodedString(bundle);

    assertNotNull(encodedString);

    Bundle outputBundle = BundleUtil.fromEncodedString(encodedString);
    Data outputData = outputBundle.getParcelable(DATA_KEY);

    assertEquals(sampleData, outputData);
  }
}
