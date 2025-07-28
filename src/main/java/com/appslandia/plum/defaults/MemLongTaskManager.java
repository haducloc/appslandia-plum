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

package com.appslandia.plum.defaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.appslandia.common.base.LruMap;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.base.LongTask;
import com.appslandia.plum.base.LongTaskManager;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class MemLongTaskManager implements LongTaskManager {

  final Map<UUID, LongTask> longTaskMap = Collections.synchronizedMap(new LruMap<>(100));

  @Override
  public void save(LongTask longTask) {
    longTask.setSeries(UUID.randomUUID());
    this.longTaskMap.put(longTask.getSeries(), copy(longTask));
  }

  @Override
  public LongTask load(UUID series) {
    var obj = this.longTaskMap.get(series);
    return (obj != null) ? copy(obj) : null;
  }

  @Override
  public void updateDone(UUID series, int status, String message, LocalDateTime doneAtUtc) {
    var obj = this.longTaskMap.get(series);
    Arguments.notNull(obj);

    obj.setStatus(status);
    obj.setMessage(message);
    obj.setDoneAtUtc(doneAtUtc);
  }

  @Override
  public void remove(UUID series) {
    this.longTaskMap.remove(series);
  }

  @Override
  public List<LongTask> query(LocalDate createdStart, LocalDate createdEnd) {
    var createdStartUtc = (createdStart != null) ? createdStart.atTime(LocalTime.MIN) : null;
    var createdEndUtc = (createdEnd != null) ? createdEnd.atTime(LocalTime.MAX) : null;

    return this.longTaskMap.values().stream()
        .filter(e -> ((createdStartUtc == null) || (e.getCreatedAtUtc().compareTo(createdStartUtc) >= 0))
            && ((createdEndUtc == null) || (e.getCreatedAtUtc().compareTo(createdEndUtc) <= 0)))
        .sorted((t1, t2) -> t2.getCreatedAtUtc().compareTo(t1.getCreatedAtUtc())).map(e -> copy(e)).toList();
  }

  static LongTask copy(LongTask obj) {
    var task = new LongTask();
    task.setSeries(obj.getSeries());
    task.setStatus(obj.getStatus());
    task.setMessage(obj.getMessage());

    task.setCreatedAtUtc(obj.getCreatedAtUtc());
    task.setDoneAtUtc(obj.getDoneAtUtc());
    return task;
  }
}
