// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.ValueUtils;

import jakarta.servlet.MultipartConfigElement;

/**
 *
 * @author Loc Ha
 *
 */
public class DynMultipartConfig extends InitializingObject {

  private String location = StringUtils.EMPTY_STRING;
  private long maxFileSize = -1;
  private long maxRequestSize = -1;
  private int fileSizeThreshold;

  @Override
  protected void init() throws Exception {
  }

  public MultipartConfigElement toMultipartConfigElement() {
    initialize();
    return new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
  }

  public DynMultipartConfig location(String location) {
    assertNotInitialized();
    this.location = StringUtils.trimToEmpty(location);
    return this;
  }

  public DynMultipartConfig maxFileSize(long maxFileSize) {
    assertNotInitialized();
    this.maxFileSize = ValueUtils.valueOrMin(maxFileSize, -1);
    return this;
  }

  public DynMultipartConfig maxRequestSize(long maxRequestSize) {
    assertNotInitialized();
    this.maxRequestSize = ValueUtils.valueOrMin(maxRequestSize, -1);
    return this;
  }

  public DynMultipartConfig fileSizeThreshold(int fileSizeThreshold) {
    assertNotInitialized();
    this.fileSizeThreshold = ValueUtils.valueOrMin(fileSizeThreshold, 0);
    return this;
  }
}
