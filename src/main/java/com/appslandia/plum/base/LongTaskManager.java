// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Loc Ha
 *
 */
public interface LongTaskManager {

  void save(LongTask longTask);

  LongTask load(UUID series);

  void updateStarted(UUID series, boolean trackingProgress, Instant startedAtUtc);

  void updateProgress(UUID series, int progressPercent);

  void updateDone(UUID series, LongTaskStatus status, String message, Instant doneAtUtc);

  void remove(UUID series);

  List<LongTask> query(LocalDate issuedStart, LocalDate issuedEnd);
}
