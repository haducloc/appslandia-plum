// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.appslandia.common.jpa.EntityBase;

import jakarta.persistence.Column;
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
public class RemMeToken extends EntityBase {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID series;

  @NotNull
  private String hashToken;

  @NotNull
  @Column(updatable = false)
  private String identity;

  @NotNull
  @Column(updatable = false)
  private String module;

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

  public String getHashToken() {
    return hashToken;
  }

  public void setHashToken(String hashToken) {
    this.hashToken = hashToken;
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
