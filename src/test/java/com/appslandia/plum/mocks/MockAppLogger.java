// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
