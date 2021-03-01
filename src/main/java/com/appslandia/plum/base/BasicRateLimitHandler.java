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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import com.appslandia.common.base.RateLimit;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.logging.AppLogger;
import com.appslandia.common.utils.DateUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class BasicRateLimitHandler extends RateLimitHandler {

	public static final String CONFIG_ACCESS_RATE1 = BasicRateLimitHandler.class.getName() + ".access_rate1";
	public static final String CONFIG_ACCESS_RATE2 = BasicRateLimitHandler.class.getName() + ".access_rate2";
	public static final String CONFIG_MAX_ACCESSES = BasicRateLimitHandler.class.getName() + ".max_accesses";

	public static final String CONFIG_BANNED_RESET_WINDOW = BasicRateLimitHandler.class.getName() + ".banned_reset_window";

	protected RateLimit accessRate1;
	protected RateLimit accessRate2;
	protected long maxAccesses;

	protected long bannedResetWindow;

	protected final ConcurrentMap<String, Tracker> clientTrackers = new ConcurrentHashMap<>();
	protected final ConcurrentMap<String, Long> bannedClients = new ConcurrentHashMap<>();

	@Inject
	protected AppConfig appConfig;

	@Inject
	protected AppLogger appLogger;

	final AtomicLong clientAccesses = new AtomicLong();

	protected abstract ScheduledExecutorService getScheduledExecutor();

	@Override
	protected void init() throws Exception {
		this.appLogger.info("Initializing {}...", getClass().getName());

		// CONFIGS
		String cfgAccessRate1 = this.appConfig.getString(CONFIG_ACCESS_RATE1, "25/500ms");
		String cfgAccessRate2 = this.appConfig.getString(CONFIG_ACCESS_RATE2, "1000/1m");
		this.maxAccesses = this.appConfig.getLong(CONFIG_MAX_ACCESSES, 0);
		String cfgBannedResetWindow = this.appConfig.getString(CONFIG_BANNED_RESET_WINDOW, "24h");

		this.appLogger.info("{}={}", CONFIG_ACCESS_RATE1, cfgAccessRate1);
		this.appLogger.info("{}={}", CONFIG_ACCESS_RATE2, cfgAccessRate2);
		this.appLogger.info("{}={}", CONFIG_MAX_ACCESSES, this.maxAccesses);
		this.appLogger.info("{}={}", CONFIG_BANNED_RESET_WINDOW, cfgBannedResetWindow);

		this.accessRate1 = RateLimit.parse(cfgAccessRate1);
		this.accessRate2 = RateLimit.parse(cfgAccessRate2);
		this.bannedResetWindow = DateUtils.translateToMs(cfgBannedResetWindow);

		this.getScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				clientTrackers.clear();
				clientAccesses.set(0);
			}
		}, 0, this.accessRate2.getWindowMs(), TimeUnit.MILLISECONDS);

		this.getScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				bannedClients.entrySet().removeIf(e -> e.getValue() <= System.currentTimeMillis());
			}
		}, 0, this.bannedResetWindow, TimeUnit.MILLISECONDS);

		this.appLogger.info("Finished initializing {}.", getClass().getName());
	}

	@Override
	protected void checkClient(String clientId) throws TooManyRequestsException {
		final long curTimeMs = System.currentTimeMillis();

		// Banned?
		Long bannedTil = this.bannedClients.get(clientId);

		if ((bannedTil != null) && (bannedTil > curTimeMs)) {
			throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TMR_ACCESS_RATE2).setReason(RateLimitResult.TMR_ACCESS_RATE2.name())
					.setRetryAfter(bannedTil);
		}

		// Max Accesses
		if ((this.maxAccesses > 0) && (this.clientAccesses.incrementAndGet() > this.maxAccesses)) {
			throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TOO_MANY_REQUESTS).setReason(RateLimitResult.TMR_MAX_ACCESSES.name())
					.setRetryAfter(curTimeMs + this.accessRate2.getWindowMs());
		}

		// Tracker
		Tracker tracker = this.clientTrackers.computeIfAbsent(clientId, k -> new Tracker());

		// Check Rate
		RateLimitResult result = tracker.checkRateLimit(curTimeMs);

		// RATE2
		if (result == RateLimitResult.TMR_ACCESS_RATE2) {
			bannedTil = curTimeMs + this.bannedResetWindow;
			this.bannedClients.put(clientId, bannedTil);

			throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TMR_ACCESS_RATE2).setReason(result.name()).setRetryAfter(bannedTil);
		}

		// RATE1
		if (result == RateLimitResult.TMR_ACCESS_RATE1) {
			throw new TooManyRequestsException().setTitleKey(Resources.ERROR_TMR_ACCESS_RATE1).setReason(result.name()).setRetryAfter(null);
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

	@ToStringBuilder.ToString
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

			// RATE2
			if (nextAccesses > accessRate2.getAccesses()) {
				return RateLimitResult.TMR_ACCESS_RATE2;
			}

			final long distanceMs = curTimeMs - this.lastCheckMs;

			// RATE1
			if (distanceMs >= accessRate1.getWindowMs()) {

				boolean allow = (1.0 * (nextAccesses - this.lastAccesses) / distanceMs) < accessRate1.getRatePerMs();

				this.lastAccesses = nextAccesses;
				this.lastCheckMs = curTimeMs;

				if (!allow) {
					return RateLimitResult.TMR_ACCESS_RATE1;
				}
			}
			return RateLimitResult.PASSED;
		}

		@Override
		public String toString() {
			return String.format("Tracker[curAccesses=%d, lastAccesses=%d, lastCheckMs=%d, isNew=%b]", this.curAccesses, this.lastAccesses, this.lastCheckMs,
					this.isNew);
		}
	}

	static enum RateLimitResult {

		PASSED, TMR_MAX_ACCESSES, TMR_ACCESS_RATE2, TMR_ACCESS_RATE1
	}
}
