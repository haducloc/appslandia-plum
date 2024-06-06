// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.base.RateLimit;
import com.appslandia.common.utils.DateUtils;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class BasicAccessRateHandler extends AccessRateHandler {

  public static final String CONFIG_ACCESS_RATE__1 = BasicAccessRateHandler.class.getName() + ".access_rate1";
  public static final String CONFIG_ACCESS_RATE_2 = BasicAccessRateHandler.class.getName() + ".access_rate2";

  public static final String CONFIG_BANNED_RESET_WINDOW = BasicAccessRateHandler.class.getName()
      + ".banned_reset_window";

  protected RateLimit accessRate1;
  protected RateLimit accessRate2;

  protected long bannedResetWindow;

  protected final ConcurrentMap<String, Tracker> clientTrackers = new ConcurrentHashMap<>();
  protected final ConcurrentMap<String, Long> bannedClients = new ConcurrentHashMap<>();

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  protected final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();

  protected abstract ScheduledExecutorService getExecutor();

  @Override
  protected void init() throws Exception {
    this.appLogger.info("Initializing {}...", getClass().getName());

    // Configs
    String cfAccessRate1 = this.appConfig.getString(CONFIG_ACCESS_RATE__1, "500/1m");
    String cfAccessRate2 = this.appConfig.getString(CONFIG_ACCESS_RATE_2, "25/500ms");
    String cfgBannedResetWindow = this.appConfig.getString(CONFIG_BANNED_RESET_WINDOW, "1h");

    this.appLogger.info("{}={}", CONFIG_ACCESS_RATE__1, cfAccessRate1);
    this.appLogger.info("{}={}", CONFIG_ACCESS_RATE_2, cfAccessRate2);
    this.appLogger.info("{}={}", CONFIG_BANNED_RESET_WINDOW, cfgBannedResetWindow);

    this.accessRate1 = RateLimit.parse(cfAccessRate1);
    this.accessRate2 = RateLimit.parse(cfAccessRate2);
    this.bannedResetWindow = DateUtils.translateToMs(cfgBannedResetWindow);

    // Clear clientTrackers
    this.scheduledTasks.add(this.getExecutor().scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        clientTrackers.clear();
      }
    }, 0, this.accessRate1.getWindowMs(), TimeUnit.MILLISECONDS));

    // Clear bannedClients
    this.scheduledTasks.add(this.getExecutor().scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        bannedClients.entrySet().removeIf(e -> e.getValue() <= System.currentTimeMillis());
      }
    }, 0, this.bannedResetWindow, TimeUnit.MILLISECONDS));

    this.appLogger.info("Finished initializing {}.", getClass().getName());
  }

  @Override
  protected void checkClient(String clientId) throws TooManyRequestsException {
    final long curTimeMs = System.currentTimeMillis();

    // Banned?
    Long bannedTil = this.bannedClients.get(clientId);

    if ((bannedTil != null) && (bannedTil > curTimeMs)) {
      throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TOO_MANY_REQUESTS_CLIENT)
          .setReason(AccessRateResult.ACCESS_RATE_1.name()).setRetryAfter(bannedTil);
    }

    // Tracker
    Tracker tracker = this.clientTrackers.computeIfAbsent(clientId, k -> new Tracker());

    // Check Rate
    AccessRateResult result = tracker.checkAccess(curTimeMs);

    // ACCESS_RATE_1
    if (result == AccessRateResult.ACCESS_RATE_1) {
      bannedTil = curTimeMs + this.bannedResetWindow;
      this.bannedClients.put(clientId, bannedTil);

      throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TOO_MANY_REQUESTS_CLIENT)
          .setReason(result.name()).setRetryAfter(bannedTil);
    }

    // ACCESS_RATE_2
    if (result == AccessRateResult.ACCESS_RATE_2) {
      throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TOO_MANY_REQUESTS_CLIENT)
          .setReason(result.name());
    }
  }

  public void clearBannedClients() {
    this.bannedClients.clear();
  }

  public Map<String, Long> getBannedClients() {
    return this.bannedClients;
  }

  public Map<String, Tracker> getClientTrackers() {
    return this.clientTrackers;
  }

  @PreDestroy
  private void dispose() {
    for (ScheduledFuture<?> scheduledTask : this.scheduledTasks) {
      scheduledTask.cancel(true);
    }
  }

  final class Tracker implements Serializable {
    private static final long serialVersionUID = 1L;

    private int curAccesses;
    private int lastAccesses;

    private long lastCheckMs = System.currentTimeMillis();
    private boolean isNew = true;

    public synchronized AccessRateResult checkAccess(long curTimeMs) {
      if (this.isNew) {
        this.isNew = false;
        return AccessRateResult.PASSED;
      }
      final int nextAccesses = ++this.curAccesses;

      // ACCESS_RATE_1
      if (nextAccesses > accessRate1.getAccesses()) {
        return AccessRateResult.ACCESS_RATE_1;
      }
      final long distanceMs = curTimeMs - this.lastCheckMs;

      // ACCESS_RATE_2
      if (distanceMs >= accessRate2.getWindowMs()) {
        boolean allowed = Double.compare((1.0d * (nextAccesses - this.lastAccesses) / distanceMs),
            accessRate2.getRatePerMs()) < 0;

        this.lastAccesses = nextAccesses;
        this.lastCheckMs = curTimeMs;

        if (!allowed) {
          return AccessRateResult.ACCESS_RATE_2;
        }
      }

      return AccessRateResult.PASSED;
    }

    @Override
    public String toString() {
      return String.format("Tracker[curAccesses=%d, lastAccesses=%d, lastCheckMs=%d, isNew=%b]", this.curAccesses,
          this.lastAccesses, this.lastCheckMs, this.isNew);
    }
  }

  static enum AccessRateResult {

    ACCESS_RATE_1, ACCESS_RATE_2, PASSED
  }
}
