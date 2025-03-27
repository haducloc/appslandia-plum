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
public class LongTask extends EntityBase {
  private static final long serialVersionUID = 1L;

  public static final int STATUS_NOT_STARTED = 0;
  public static final int STATUS_IN_PROGRESS = 1;
  public static final int STATUS_COMPLETED_SUCCESS = 2;
  public static final int STATUS_COMPLETED_FAILURE = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID series;

  @NotNull
  @Column(updatable = false)
  private String title;

  @NotNull
  private Integer status;

  @NotNull
  private String message;

  @NotNull
  @Column(updatable = false)
  private LocalDateTime createdAtUtc;

  private LocalDateTime doneAtUtc;

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

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getCreatedAtUtc() {
    return this.createdAtUtc;
  }

  public void setCreatedAtUtc(LocalDateTime createdAtUtc) {
    this.createdAtUtc = createdAtUtc;
  }

  public LocalDateTime getDoneAtUtc() {
    return this.doneAtUtc;
  }

  public void setDoneAtUtc(LocalDateTime doneAtUtc) {
    this.doneAtUtc = doneAtUtc;
  }
}
