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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 */
public class JulAppLogger implements AppLogger {

  private final Logger logger;

  public JulAppLogger(Logger logger) {
    this.logger = logger;
  }

  public JulAppLogger(Class<?> clazz) {
    logger = Logger.getLogger(clazz.getName());
  }

  public JulAppLogger(String name) {
    logger = Logger.getLogger(name);
  }

  @Override
  public boolean isLoggable(LogLevel level) {
    Arguments.notNull(level);

    return switch (level) {
    case TRACE -> logger.isLoggable(Level.FINER);
    case DEBUG -> logger.isLoggable(Level.FINE);
    case INFO -> logger.isLoggable(Level.INFO);
    case WARN -> logger.isLoggable(Level.WARNING);
    case ERROR -> logger.isLoggable(Level.SEVERE);
    case ALL -> true;
    case OFF -> false;
    default -> false;
    };
  }

  @Override
  public void trace(String message) {
    logger.log(Level.FINER, message);
  }

  @Override
  public void debug(String message) {
    logger.log(Level.FINE, message);
  }

  @Override
  public void info(String message) {
    logger.log(Level.INFO, message);
  }

  @Override
  public void warn(String message) {
    logger.log(Level.WARNING, message);
  }

  @Override
  public void error(String message) {
    logger.log(Level.SEVERE, message);
  }

  // --- Supplier-based (lazy evaluation, Java 9+) ---
  @Override
  public void trace(Supplier<String> message) {
    logger.log(Level.FINER, message);
  }

  @Override
  public void debug(Supplier<String> message) {
    logger.log(Level.FINE, message);
  }

  @Override
  public void info(Supplier<String> message) {
    logger.log(Level.INFO, message);
  }

  @Override
  public void warn(Supplier<String> message) {
    logger.log(Level.WARNING, message);
  }

  @Override
  public void error(Supplier<String> message) {
    logger.log(Level.SEVERE, message);
  }

  // --- Message + Throwable ---
  @Override
  public void trace(String message, Throwable thrown) {
    logger.log(Level.FINER, message, thrown);
  }

  @Override
  public void debug(String message, Throwable thrown) {
    logger.log(Level.FINE, message, thrown);
  }

  @Override
  public void info(String message, Throwable thrown) {
    logger.log(Level.INFO, message, thrown);
  }

  @Override
  public void warn(String message, Throwable thrown) {
    logger.log(Level.WARNING, message, thrown);
  }

  @Override
  public void error(String message, Throwable thrown) {
    logger.log(Level.SEVERE, message, thrown);
  }

  @Override
  public void trace(Supplier<String> message, Throwable thrown) {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, message.get(), thrown);
    }
  }

  @Override
  public void debug(Supplier<String> message, Throwable thrown) {
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, message.get(), thrown);
    }
  }

  @Override
  public void info(Supplier<String> message, Throwable thrown) {
    if (logger.isLoggable(Level.INFO)) {
      logger.log(Level.INFO, message.get(), thrown);
    }
  }

  @Override
  public void warn(Supplier<String> message, Throwable thrown) {
    if (logger.isLoggable(Level.WARNING)) {
      logger.log(Level.WARNING, message.get(), thrown);
    }
  }

  @Override
  public void error(Supplier<String> message, Throwable thrown) {
    if (logger.isLoggable(Level.SEVERE)) {
      logger.log(Level.SEVERE, message.get(), thrown);
    }
  }

  @Override
  public String toString() {
    return "JulAppLogger[" + logger.getName() + "]";
  }
}
