// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.appslandia.common.jpa.EntityBase;

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
  public static final String AUTH_TYPE_REMEMBER_ME = "remember_me";

  // Results
  public static final String AUTH_RESULT_SUCCESS = "success";

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

  private UUID series;

  private String callerUniqueId;

  @NotNull
  private Instant authAtUtc;

  private String sessionId;

  @NotNull
  private String requestId;

  @Override
  public Serializable getPk() {
    return authEventId;
  }

  public UUID getAuthEventId() {
    return authEventId;
  }

  public void setAuthEventId(UUID authEventId) {
    this.authEventId = authEventId;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public String getAuthResult() {
    return authResult;
  }

  public void setAuthResult(String authResult) {
    this.authResult = authResult;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public UUID getSeries() {
    return series;
  }

  public void setSeries(UUID series) {
    this.series = series;
  }

  public String getCallerUniqueId() {
    return callerUniqueId;
  }

  public void setCallerUniqueId(String callerUniqueId) {
    this.callerUniqueId = callerUniqueId;
  }

  public Instant getAuthAtUtc() {
    return authAtUtc;
  }

  public void setAuthAtUtc(Instant authAtUtc) {
    this.authAtUtc = authAtUtc;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
