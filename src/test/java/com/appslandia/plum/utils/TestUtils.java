// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.base.MemoryStream;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockHttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class TestUtils {

  public static void initDeployEnv(DeployEnv env) {
    var field = ReflectionUtils.findField(DeployEnv.class, "current");
    Asserts.notNull(field);

    ReflectionUtils.set(field, null, env);
  }

  public static String unbrotliToString(MemoryStream brContent) throws IOException {
    if (brContent.length() == 0) {
      return StringUtils.EMPTY_STRING;
    }

    try (var bis = new ByteArrayInputStream(brContent.toByteArray()); var bris = new BrotliInputStream(bis)) {
      return IOUtils.toString(bris, StandardCharsets.UTF_8.name());
    }
  }

  public static String ungzipToString(MemoryStream gzipContent) throws IOException {
    if (gzipContent.length() == 0) {
      return StringUtils.EMPTY_STRING;
    }
    try (var gis = new GZIPInputStream(new ByteArrayInputStream(gzipContent.toByteArray()))) {
      return IOUtils.toString(gis, StandardCharsets.UTF_8.name());
    }
  }

  public static String undeflateToString(MemoryStream deflateContent) throws IOException {
    if (deflateContent.length() == 0) {
      return StringUtils.EMPTY_STRING;
    }

    try (var iis = new InflaterInputStream(new ByteArrayInputStream(deflateContent.toByteArray()),
        new Inflater(false))) {
      return IOUtils.toString(iis, StandardCharsets.UTF_8.name());
    }
  }

  public static void printException(MockHttpServletRequest request) {
    var error = (Throwable) request.getAttribute(Throwable.class.getName());
    if (error != null) {
      error.printStackTrace();
    }
  }

  public static void printHeaders(MockHttpServletRequest request) {
    System.out.println(new ToStringBuilder().toStringHeaders(request));
  }

  public static void printHeaders(MockHttpServletResponse response) {
    System.out.println(new ToStringBuilder().toStringHeaders(response));
  }
}
