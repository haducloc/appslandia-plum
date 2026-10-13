// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.appslandia.common.base.LruMap;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.LongTask;
import com.appslandia.plum.base.LongTaskManager;
import com.appslandia.plum.base.LongTaskStatus;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultLongTaskManager implements LongTaskManager {

  final Map<UUID, LongTask> longTaskMap = new LruMap<>(100);

  @Override
  public synchronized void save(LongTask longTask) {
    if (longTask.getSeries() == null) {
      longTask.setSeries(UUID.randomUUID());
    }
    longTaskMap.put(longTask.getSeries(), copy(longTask));
  }

  @Override
  public synchronized LongTask load(UUID series) {
    var obj = longTaskMap.get(series);
    return (obj != null) ? copy(obj) : null;
  }

  @Override
  public synchronized void updateStarted(UUID series, boolean trackingProgress, Instant startedAtUtc) {
    var obj = longTaskMap.get(series);
    Arguments.notNull(obj);

    obj.setStatus(LongTaskStatus.IN_PROGRESS);
    if (trackingProgress) {
      obj.setProgressPercent(0);
    }
    obj.setStartedAtUtc(startedAtUtc);
  }

  @Override
  public synchronized void updateProgress(UUID series, int progressPercent) {
    Arguments.isTrue(progressPercent >= 0 && progressPercent <= 100, "progressPercent >= 0 && progressPercent <= 100.");

    var obj = longTaskMap.get(series);
    Arguments.notNull(obj);
    Asserts.isTrue(obj.getStatus() == LongTaskStatus.IN_PROGRESS);

    obj.setProgressPercent(progressPercent);
  }

  @Override
  public synchronized void updateDone(UUID series, LongTaskStatus status, String message, Instant doneAtUtc) {
    Arguments.isTrue(LongTaskStatus.DONE_SUCCESS == status || LongTaskStatus.DONE_FAILURE == status
        || LongTaskStatus.DONE_CANCELLED == status);

    var obj = longTaskMap.get(series);
    Arguments.notNull(obj);
    Asserts.isTrue(obj.getStatus() == LongTaskStatus.IN_PROGRESS || obj.getStatus() == LongTaskStatus.NOT_STARTED);

    obj.setStatus(status);
    obj.setMessage(message);
    obj.setDoneAtUtc(doneAtUtc);

    if (LongTaskStatus.DONE_SUCCESS == status) {
      if (obj.getProgressPercent() != null) {
        obj.setProgressPercent(100);
      }
    }
  }

  @Override
  public synchronized void remove(UUID series) {
    longTaskMap.remove(series);
  }

  @Override
  public synchronized List<LongTask> query(LocalDate issuedStart, LocalDate issuedEnd) {
    var issuedStartUtc = (issuedStart != null) ? issuedStart.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    var issuedEndUtc = (issuedEnd != null) ? issuedEnd.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

    // @formatter:off
    return longTaskMap.values().stream()
        .filter(e ->
                  ((issuedStartUtc == null) || (e.getIssuedAtUtc().compareTo(issuedStartUtc) >= 0)) &&
                  ((issuedEndUtc == null) || (e.getIssuedAtUtc().compareTo(issuedEndUtc) < 0))
              )
        .sorted(Comparator.comparing(LongTask::getIssuedAtUtc).reversed())
        .map(e -> copy(e))
        .toList();
    // @formatter:on
  }

  static LongTask copy(LongTask obj) {
    var task = new LongTask();
    task.setSeries(obj.getSeries());

    task.setTitle(obj.getTitle());
    task.setTaskType(obj.getTaskType());
    task.setTaskInput(obj.getTaskInput());
    task.setInitiatedBy(obj.getInitiatedBy());

    task.setStatus(obj.getStatus());
    task.setMessage(obj.getMessage());
    task.setProgressPercent(obj.getProgressPercent());

    task.setIssuedAtUtc(obj.getIssuedAtUtc());
    task.setStartedAtUtc(obj.getStartedAtUtc());
    task.setDoneAtUtc(obj.getDoneAtUtc());

    task.setErrorDetails(obj.getErrorDetails());
    return task;
  }
}
