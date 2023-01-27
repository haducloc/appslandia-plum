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
import java.util.concurrent.atomic.AtomicLong;

import com.appslandia.common.base.RateLimit;
import com.appslandia.common.logging.AppLogger;
import com.appslandia.common.utils.DateUtils;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class BasicRateLimitHandler extends RateLimitHandler {

    public static final String CONFIG_MAX_ACCESSES = BasicRateLimitHandler.class.getName() + ".max_accesses";
    public static final String CONFIG_RATE_LIMIT_1 = BasicRateLimitHandler.class.getName() + ".rate_limit_1";
    public static final String CONFIG_RATE_LIMIT_2 = BasicRateLimitHandler.class.getName() + ".rate_limit_2";

    public static final String CONFIG_BANNED_RESET_WINDOW = BasicRateLimitHandler.class.getName() + ".banned_reset_window";

    protected long maxAccesses;
    protected RateLimit rateLimit1;
    protected RateLimit rateLimit2;

    protected long bannedResetWindow;

    protected final ConcurrentMap<String, Tracker> clientTrackers = new ConcurrentHashMap<>();
    protected final ConcurrentMap<String, Long> bannedClients = new ConcurrentHashMap<>();

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected AppLogger appLogger;

    final AtomicLong clientAccesses = new AtomicLong();

    protected final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();

    protected abstract ScheduledExecutorService getScheduledExecutor();

    @Override
    protected void init() throws Exception {
	this.appLogger.info("Initializing {}...", getClass().getName());

	// CONFIGS
	this.maxAccesses = this.appConfig.getLong(CONFIG_MAX_ACCESSES, 50000);
	String cfRateLimit1 = this.appConfig.getString(CONFIG_RATE_LIMIT_1, "500/1m");
	String cfRateLimit2 = this.appConfig.getString(CONFIG_RATE_LIMIT_2, "25/500ms");
	String cfgBannedResetWindow = this.appConfig.getString(CONFIG_BANNED_RESET_WINDOW, "1h");

	this.appLogger.info("{}={}", CONFIG_MAX_ACCESSES, this.maxAccesses);
	this.appLogger.info("{}={}", CONFIG_RATE_LIMIT_1, cfRateLimit1);
	this.appLogger.info("{}={}", CONFIG_RATE_LIMIT_2, cfRateLimit2);
	this.appLogger.info("{}={}", CONFIG_BANNED_RESET_WINDOW, cfgBannedResetWindow);

	this.rateLimit1 = RateLimit.parse(cfRateLimit1);
	this.rateLimit2 = RateLimit.parse(cfRateLimit2);
	this.bannedResetWindow = DateUtils.translateToMs(cfgBannedResetWindow);

	// Reset clients schedule
	this.scheduledTasks.add(this.getScheduledExecutor().scheduleAtFixedRate(new Runnable() {
	    @Override
	    public void run() {
		clientAccesses.set(0);
		clientTrackers.clear();
	    }
	}, 0, this.rateLimit1.getWindowMs(), TimeUnit.MILLISECONDS));

	// Remove banned clients schedule
	this.scheduledTasks.add(this.getScheduledExecutor().scheduleAtFixedRate(new Runnable() {
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
	    throw new TooManyRequestsException().setTitleKey(Resources.ERROR_RATE_LIMIT_1).setReason(RateLimitResult.RATE_LIMIT_1.name()).setRetryAfter(bannedTil);
	}

	// Max Accesses
	if (this.clientAccesses.incrementAndGet() > this.maxAccesses) {
	    throw new TooManyRequestsException().setTitleKey(Resources.ERROR_MAX_ACCESSES).setReason(RateLimitResult.MAX_ACCESSES.name());
	}

	// Tracker
	Tracker tracker = this.clientTrackers.computeIfAbsent(clientId, k -> new Tracker());

	// Check Rate
	RateLimitResult result = tracker.checkRateLimit(curTimeMs);

	// Level 1
	if (result == RateLimitResult.RATE_LIMIT_1) {
	    bannedTil = curTimeMs + this.bannedResetWindow;
	    this.bannedClients.put(clientId, bannedTil);

	    throw new TooManyRequestsException().setTitleKey(Resources.ERROR_RATE_LIMIT_1).setReason(result.name()).setRetryAfter(bannedTil);
	}

	// Level 2
	if (result == RateLimitResult.RATE_LIMIT_2) {
	    throw new TooManyRequestsException().setTitleKey(Resources.ERROR_RATE_LIMIT_2).setReason(result.name());
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

	public synchronized RateLimitResult checkRateLimit(long curTimeMs) {
	    if (this.isNew) {
		this.isNew = false;
		return RateLimitResult.PASSED;
	    }
	    final int nextAccesses = ++this.curAccesses;

	    // Level 1
	    if (nextAccesses > rateLimit1.getAccesses()) {
		return RateLimitResult.RATE_LIMIT_1;
	    }

	    final long distanceMs = curTimeMs - this.lastCheckMs;

	    // Level 2
	    if (distanceMs >= rateLimit2.getWindowMs()) {

		boolean allow = (1.0 * (nextAccesses - this.lastAccesses) / distanceMs) < rateLimit2.getRatePerMs();

		this.lastAccesses = nextAccesses;
		this.lastCheckMs = curTimeMs;

		if (!allow) {
		    return RateLimitResult.RATE_LIMIT_2;
		}
	    }

	    // Pass
	    return RateLimitResult.PASSED;
	}

	@Override
	public String toString() {
	    return String.format("Tracker[curAccesses=%d, lastAccesses=%d, lastCheckMs=%d, isNew=%b]", this.curAccesses, this.lastAccesses, this.lastCheckMs, this.isNew);
	}
    }

    static enum RateLimitResult {

	MAX_ACCESSES, RATE_LIMIT_1, RATE_LIMIT_2, PASSED
    }
}
