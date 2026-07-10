// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 *
 * @author Loc Ha
 *
 */
public class MockServletInputStream extends ServletInputStream {

  final InputStream is;

  public MockServletInputStream(InputStream is) {
    this.is = is;
  }

  @Override
  public int read() throws IOException {
    return is.read();
  }

  @Override
  public synchronized void reset() throws IOException {
    is.reset();
  }

  @Override
  public void close() throws IOException {
    is.close();
    super.close();
  }

  @Override
  public boolean isFinished() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReady() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setReadListener(ReadListener readListener) {
    throw new UnsupportedOperationException();
  }
}
