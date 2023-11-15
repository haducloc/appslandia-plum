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

import com.appslandia.common.models.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Entity
public class LongTask extends EntityBase {
  private static final long serialVersionUID = 1L;

  public static final String IN_PROGRESS = "in_progress";
  public static final String DONE_SUCCESS = "done_success";
  public static final String DONE_FAILURE = "done_failure";

  @Id
  @NotNull
  @Column(unique = true, updatable = false)
  private String series;

  @NotNull
  private String title;

  @NotNull
  private String status;

  @NotNull
  private String message;

  @NotNull
  @Column(updatable = false)
  private Long createdAt;

  private Long doneAt;

  @Override
  public Serializable getPk() {
    return this.series;
  }

  public String getSeries() {
    return this.series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }

  public Long getDoneAt() {
    return this.doneAt;
  }

  public void setDoneAt(Long doneAt) {
    this.doneAt = doneAt;
  }
}
