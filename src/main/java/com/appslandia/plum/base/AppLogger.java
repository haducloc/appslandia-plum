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

package com.appslandia.plum.base;

import java.util.function.Supplier;

import com.appslandia.common.base.TaskBlock;
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.STR;

/**
 *
 * @author Loc Ha
 *
 */
public interface AppLogger {

  default void error(Throwable thrown) {
    if (isLoggable(LogLevel.ERROR)) {
      error(ExceptionUtils.buildMessage(thrown), thrown);
    }
  }

  default void error(TaskBlock taskBlock) {
    try {
      taskBlock.run();

    } catch (Exception thrown) {
      if (isLoggable(LogLevel.ERROR)) {
        error(ExceptionUtils.buildMessage(thrown), thrown);
      }
    }
  }

  boolean isLoggable(LogLevel level);

  default void trace(String format, Object... params) {
    if (isLoggable(LogLevel.TRACE)) {
      var message = STR.format(format, params);
      trace(message);
    }
  }

  default void debug(String format, Object... params) {
    if (isLoggable(LogLevel.DEBUG)) {
      var message = STR.format(format, params);
      debug(message);
    }
  }

  default void info(String format, Object... params) {
    if (isLoggable(LogLevel.INFO)) {
      var message = STR.format(format, params);
      info(message);
    }
  }

  default void warn(String format, Object... params) {
    if (isLoggable(LogLevel.WARN)) {
      var message = STR.format(format, params);
      warn(message);
    }
  }

  default void error(String format, Object... params) {
    if (isLoggable(LogLevel.ERROR)) {
      var message = STR.format(format, params);
      error(message);
    }
  }

  void trace(String message);

  void debug(String message);

  void info(String message);

  void warn(String message);

  void error(String message);

  void trace(Supplier<String> message);

  void debug(Supplier<String> message);

  void info(Supplier<String> message);

  void warn(Supplier<String> message);

  void error(Supplier<String> message);

  void trace(String message, Throwable thrown);

  void debug(String message, Throwable thrown);

  void info(String message, Throwable thrown);

  void warn(String message, Throwable thrown);

  void error(String message, Throwable thrown);

  void trace(Supplier<String> message, Throwable thrown);

  void debug(Supplier<String> message, Throwable thrown);

  void info(Supplier<String> message, Throwable thrown);

  void warn(Supplier<String> message, Throwable thrown);

  void error(Supplier<String> message, Throwable thrown);

  enum LogLevel {

    OFF(0), ERROR(200), WARN(300), INFO(400), DEBUG(500), TRACE(600), ALL(Integer.MAX_VALUE);

    private final int value;

    LogLevel(int value) {
      this.value = value;
    }

    public int value() {
      return value;
    }
  }
}
