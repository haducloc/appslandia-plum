// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.utils.RandomUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class EtagUtilsTest {

  @Test
  public void test_toEtag() {
    var content = RandomUtils.nextBytes(100, new Random());
    var md5 = new DigesterImpl("MD5").digest(content);
    var weakEtag = EtagUtils.toStrongEtag(md5);

    Assertions.assertTrue(weakEtag.length() == 34);
    Assertions.assertTrue(weakEtag.startsWith("\""));
    Assertions.assertTrue(weakEtag.endsWith("\""));
  }
}
