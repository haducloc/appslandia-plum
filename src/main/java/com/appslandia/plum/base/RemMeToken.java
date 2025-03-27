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
  private LocalDateTime expiresAtUtc;

  @NotNull
  private LocalDateTime issuedAtUtc;

  @Override
  public Serializable getPk() {
    return this.series;
  }

  public UUID getSeries() {
    return this.series;
  }

  public void setSeries(UUID series) {
    this.series = series;
  }

  public String getHashToken() {
    return this.hashToken;
  }

  public void setHashToken(String hashToken) {
    this.hashToken = hashToken;
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

  public LocalDateTime getExpiresAtUtc() {
    return this.expiresAtUtc;
  }

  public void setExpiresAtUtc(LocalDateTime expiresAtUtc) {
    this.expiresAtUtc = expiresAtUtc;
  }

  public LocalDateTime getIssuedAtUtc() {
    return this.issuedAtUtc;
  }

  public void setIssuedAtUtc(LocalDateTime issuedAtUtc) {
    this.issuedAtUtc = issuedAtUtc;
  }
}
