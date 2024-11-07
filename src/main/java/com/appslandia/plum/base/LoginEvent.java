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
import java.time.LocalDateTime;
import java.util.UUID;

import com.appslandia.common.models.EntityBase;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Entity
public class LoginEvent extends EntityBase {
  private static final long serialVersionUID = 1L;

  public static final int LOGIN_TYPE_FORM = 1;
  public static final int LOGIN_TYPE_REMEMBER_ME = 2;
  public static final int LOGIN_TYPE_CODE = 3;

  public static final int LOGIN_RESULT_SUCCESS = 0;
  public static final int LOGIN_FAILURE_FORM = 1;
  public static final int LOGIN_FAILURE_REMEMBER_ME_TOKEN_COMPROMISED = 2;
  public static final int LOGIN_FAILURE_CODE = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID loginEventId;

  @NotNull
  private String identity;

  @NotNull
  private String module;

  @NotNull
  private Integer loginResult;

  @NotNull
  private Integer eventType;

  @NotNull
  private String clientIp;

  private String userAgent;

  private UUID series;

  @NotNull
  private LocalDateTime loginAtUtc;

  @Override
  public Serializable getPk() {
    return this.loginEventId;
  }

  public UUID getLoginEventId() {
    return this.loginEventId;
  }

  public void setLoginEventId(UUID loginEventId) {
    this.loginEventId = loginEventId;
  }

  public String getIdentity() {
    return this.identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getModule() {
    return this.module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public Integer getLoginResult() {
    return this.loginResult;
  }

  public void setLoginResult(Integer loginResult) {
    this.loginResult = loginResult;
  }

  public Integer getEventType() {
    return this.eventType;
  }

  public void setEventType(Integer eventType) {
    this.eventType = eventType;
  }

  public String getClientIp() {
    return this.clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public UUID getSeries() {
    return this.series;
  }

  public void setSeries(UUID series) {
    this.series = series;
  }

  public LocalDateTime getLoginAtUtc() {
    return this.loginAtUtc;
  }

  public void setLoginAtUtc(LocalDateTime loginAtUtc) {
    this.loginAtUtc = loginAtUtc;
  }
}
