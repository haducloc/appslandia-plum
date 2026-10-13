// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.appslandia.common.jpa.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Loc Ha
 *
 */
@Entity
public class OAuth2State extends EntityBase {
  private static final long serialVersionUID = 1L;

  @Id
  private UUID series;

  @NotNull
  @Column(updatable = false)
  private String codeVerifier;

  @NotNull
  @Column(updatable = false)
  private String module;

  @NotNull
  @Column(updatable = false)
  private String provider;

  @Column(updatable = false)
  private String returnUrl;

  @NotNull
  @Column(updatable = false)
  private String integrityHash;

  @NotNull
  @Column(updatable = false)
  private Instant expiresAtUtc;

  @NotNull
  @Column(updatable = false)
  private Instant issuedAtUtc;

  @Override
  public Serializable getPk() {
    return series;
  }

  public UUID getSeries() {
    return series;
  }

  public void setSeries(UUID series) {
    this.series = series;
  }

  public String getCodeVerifier() {
    return codeVerifier;
  }

  public void setCodeVerifier(String codeVerifier) {
    this.codeVerifier = codeVerifier;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public void setReturnUrl(String returnUrl) {
    this.returnUrl = returnUrl;
  }

  public String getIntegrityHash() {
    return integrityHash;
  }

  public void setIntegrityHash(String integrityHash) {
    this.integrityHash = integrityHash;
  }

  public Instant getExpiresAtUtc() {
    return expiresAtUtc;
  }

  public void setExpiresAtUtc(Instant expiresAtUtc) {
    this.expiresAtUtc = expiresAtUtc;
  }

  public Instant getIssuedAtUtc() {
    return issuedAtUtc;
  }

  public void setIssuedAtUtc(Instant issuedAtUtc) {
    this.issuedAtUtc = issuedAtUtc;
  }
}
