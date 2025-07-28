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
 * @author Loc Ha
 *
 */
@Entity
public class AuthEvent extends EntityBase {
  private static final long serialVersionUID = 1L;

  // Types
  public static final String AUTH_TYPE_FORM = "form";
  public static final String AUTH_TYPE_REMME = "remember_me";

  // Results
  public static final String AUTH_RESULT_SUCCESS = "success";
  public static final String AUTH_RESULT_FAILURE = "failure";
  public static final String AUTH_RESULT_TOKEN_COMP = "token_compromised";

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID authEventId;

  @NotNull
  private String identity;

  @NotNull
  private String module;

  @NotNull
  private String authType;

  @NotNull
  private String authResult;

  @NotNull
  private String clientIp;

  private String userAgent;

  private UUID remmeSeries;

  @NotNull
  private LocalDateTime authAtUtc;

  @Override
  public Serializable getPk() {
    return this.authEventId;
  }

  public UUID getAuthEventId() {
    return this.authEventId;
  }

  public void setAuthEventId(UUID authEventId) {
    this.authEventId = authEventId;
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

  public String getAuthType() {
    return this.authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public String getAuthResult() {
    return this.authResult;
  }

  public void setAuthResult(String authResult) {
    this.authResult = authResult;
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

  public UUID getRemmeSeries() {
    return this.remmeSeries;
  }

  public void setRemmeSeries(UUID remmeSeries) {
    this.remmeSeries = remmeSeries;
  }

  public LocalDateTime getAuthAtUtc() {
    return this.authAtUtc;
  }

  public void setAuthAtUtc(LocalDateTime authAtUtc) {
    this.authAtUtc = authAtUtc;
  }
}
