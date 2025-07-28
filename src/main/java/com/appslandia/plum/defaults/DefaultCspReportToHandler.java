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

import java.io.BufferedReader;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.base.LruMap;
import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.CspReportToHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultCspReportToHandler implements CspReportToHandler {

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected AppConfig appConfig;

  final TextDigester md5Digester = new TextDigester(new DigesterImpl("MD5"));

  final Map<String, Long> reportedLog = Collections.synchronizedMap(new LruMap<>(100));

  public static final String CONFIG_REPORT_INTERVAL_MS = DefaultCspReportToHandler.class.getName()
      + ".report_interval_ms";

  protected long getReportIntervalMs() {
    return this.appConfig.getLong(CONFIG_REPORT_INTERVAL_MS, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
  }

  @Override
  public void handle(BufferedReader content) throws Exception {
    var reportData = new StringBuilder(512);
    content.lines().forEach(line -> reportData.append(line));

    var json = reportData.toString();
    var md5 = this.md5Digester.digest(json);
    var curTime = System.currentTimeMillis();

    var time = this.reportedLog.compute(md5, (md, prevTime) -> {
      if ((prevTime == null) || (curTime - prevTime > getReportIntervalMs())) {
        return curTime;
      }
      return prevTime;
    });

    this.appLogger.warn("CSP reported md5: " + md5);

    if (curTime == time) {
      this.appLogger.warn(json);
    }
  }
}
