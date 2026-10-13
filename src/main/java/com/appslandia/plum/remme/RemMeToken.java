// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.remme;

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
public class RemMeToken extends EntityBase {
  private static final long serialVersionUID = 1L;

  @Id
  private UUID series;

  @NotNull
  private String tokenHash;

  @NotNull
  @Column(updatable = false)
  private String identity;

  @NotNull
  @Column(updatable = false)
  private String module;

  @NotNull
  private String integrityHash;

  @NotNull
  private Instant expiresAtUtc;

  @NotNull
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

  public String getTokenHash() {
    return tokenHash;
  }

  public void setTokenHash(String tokenHash) {
    this.tokenHash = tokenHash;
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
