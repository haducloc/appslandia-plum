// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class Log4jAppLogger implements AppLogger {

  final Logger logger;

  public Log4jAppLogger(Logger logger) {
    this.logger = logger;
  }

  public Log4jAppLogger(Class<?> clazz) {
    logger = LogManager.getLogger(clazz);
  }

  public Log4jAppLogger(String name) {
    logger = LogManager.getLogger(name);
  }

  @Override
  public boolean isLoggable(LogLevel level) {
    Arguments.notNull(level);

    return switch (level) {
    case TRACE -> logger.isTraceEnabled();
    case DEBUG -> logger.isDebugEnabled();
    case INFO -> logger.isInfoEnabled();
    case WARN -> logger.isWarnEnabled();
    case ERROR -> logger.isErrorEnabled();
    case ALL -> true;
    case OFF -> false;
    default -> false;
    };
  }

  @Override
  public void trace(String message) {
    logger.trace(message);
  }

  @Override
  public void debug(String message) {
    logger.debug(message);
  }

  @Override
  public void info(String message) {
    logger.info(message);
  }

  @Override
  public void warn(String message) {
    logger.warn(message);
  }

  @Override
  public void error(String message) {
    logger.error(message);
  }

  @Override
  public void trace(Supplier<String> message) {
    if (logger.isTraceEnabled()) {
      logger.trace(message.get());
    }
  }

  @Override
  public void debug(Supplier<String> message) {
    if (logger.isDebugEnabled()) {
      logger.debug(message.get());
    }
  }

  @Override
  public void info(Supplier<String> message) {
    if (logger.isInfoEnabled()) {
      logger.info(message.get());
    }
  }

  @Override
  public void warn(Supplier<String> message) {
    if (logger.isWarnEnabled()) {
      logger.warn(message.get());
    }
  }

  @Override
  public void error(Supplier<String> message) {
    if (logger.isErrorEnabled()) {
      logger.error(message.get());
    }
  }

  @Override
  public void trace(String message, Throwable thrown) {
    logger.trace(message, thrown);
  }

  @Override
  public void debug(String message, Throwable thrown) {
    logger.debug(message, thrown);
  }

  @Override
  public void info(String message, Throwable thrown) {
    logger.info(message, thrown);
  }

  @Override
  public void warn(String message, Throwable thrown) {
    logger.warn(message, thrown);
  }

  @Override
  public void error(String message, Throwable thrown) {
    logger.error(message, thrown);
  }

  @Override
  public void trace(Supplier<String> message, Throwable thrown) {
    if (logger.isTraceEnabled()) {
      logger.trace(message.get(), thrown);
    }
  }

  @Override
  public void debug(Supplier<String> message, Throwable thrown) {
    if (logger.isDebugEnabled()) {
      logger.debug(message.get(), thrown);
    }
  }

  @Override
  public void info(Supplier<String> message, Throwable thrown) {
    if (logger.isInfoEnabled()) {
      logger.info(message.get(), thrown);
    }
  }

  @Override
  public void warn(Supplier<String> message, Throwable thrown) {
    if (logger.isWarnEnabled()) {
      logger.warn(message.get(), thrown);
    }
  }

  @Override
  public void error(Supplier<String> message, Throwable thrown) {
    if (logger.isErrorEnabled()) {
      logger.error(message.get(), thrown);
    }
  }

  @Override
  public String toString() {
    return "Log4jAppLogger[" + logger.getName() + "]";
  }
}
