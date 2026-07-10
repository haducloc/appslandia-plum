// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.StringOutput;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlUtilsTest {

  @Test
  public void test_escAttribute() {
    var out = new StringOutput();
    try {
      HtmlUtils.escAttribute(out, "title", "test title");
      Assertions.assertTrue(out.toString().contains("title=\"test title\""));

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_escAttribute_ESC() {
    var out = new StringOutput();
    try {
      HtmlUtils.escAttribute(out, "title", "test < title");
      Assertions.assertTrue(out.toString().contains("title=\"test &lt; title\""));

    } catch (IOException ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
