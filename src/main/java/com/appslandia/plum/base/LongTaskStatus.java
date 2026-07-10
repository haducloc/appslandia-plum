// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

/**
 *
 * @author Loc Ha
 *
 */
public enum LongTaskStatus {

  NOT_STARTED(0), IN_PROGRESS(1),

  DONE_CANCELLED(2), DONE_SUCCESS(3), DONE_FAILURE(4);

  final int value;

  LongTaskStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static LongTaskStatus fromValue(int value) {
    for (var e : values()) {
      if (e.value == value) {
        return e;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
