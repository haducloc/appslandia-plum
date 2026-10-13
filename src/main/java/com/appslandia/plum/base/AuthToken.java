// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

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
public class AuthToken extends EntityBase {
  private static final long serialVersionUID = 1L;

  @Id
  private UUID series;

  @NotNull
  @Column(updatable = false)
  private String module;

  @Column(updatable = false)
  private String identity;

  @Column(updatable = false)
  private String data;

  private String codeHash;

  private Integer codeAttempts;

  @Column(updatable = false)
  private Integer maxAttempts;

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

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getCodeHash() {
    return codeHash;
  }

  public void setCodeHash(String codeHash) {
    this.codeHash = codeHash;
  }

  public Integer getCodeAttempts() {
    return codeAttempts;
  }

  public void setCodeAttempts(Integer codeAttempts) {
    this.codeAttempts = codeAttempts;
  }

  public Integer getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(Integer maxAttempts) {
    this.maxAttempts = maxAttempts;
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
