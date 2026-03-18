// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.mocks;

import java.util.function.Supplier;

import com.appslandia.plum.base.AppLogger;

/**
 *
 * @author Loc Ha
 *
 */
public class MockAppLogger implements AppLogger {

  @Override
  public boolean isLoggable(LogLevel level) {
    return false;
  }

  @Override
  public void trace(String format, Object... params) {
  }

  @Override
  public void debug(String format, Object... params) {
  }

  @Override
  public void info(String format, Object... params) {
  }

  @Override
  public void warn(String format, Object... params) {
  }

  @Override
  public void error(String format, Object... params) {
  }

  @Override
  public void trace(String message) {
  }

  @Override
  public void debug(String message) {
  }

  @Override
  public void info(String message) {
  }

  @Override
  public void warn(String message) {
  }

  @Override
  public void error(String message) {
  }

  @Override
  public void trace(Supplier<String> message) {
  }

  @Override
  public void debug(Supplier<String> message) {
  }

  @Override
  public void info(Supplier<String> message) {
  }

  @Override
  public void warn(Supplier<String> message) {
  }

  @Override
  public void error(Supplier<String> message) {
  }

  @Override
  public void trace(String message, Throwable thrown) {
  }

  @Override
  public void debug(String message, Throwable thrown) {
  }

  @Override
  public void info(String message, Throwable thrown) {
  }

  @Override
  public void warn(String message, Throwable thrown) {
  }

  @Override
  public void error(String message, Throwable thrown) {
  }

  @Override
  public void trace(Supplier<String> message, Throwable thrown) {
  }

  @Override
  public void debug(Supplier<String> message, Throwable thrown) {
  }

  @Override
  public void info(Supplier<String> message, Throwable thrown) {
  }

  @Override
  public void warn(Supplier<String> message, Throwable thrown) {
  }

  @Override
  public void error(Supplier<String> message, Throwable thrown) {
  }
}
