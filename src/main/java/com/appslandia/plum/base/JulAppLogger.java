// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
