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
public class LongTask extends EntityBase {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID series;

  @NotNull
  @Column(updatable = false, length = 500)
  private String title;

  @Column(updatable = false, length = 100)
  private String taskType;

  @Column(updatable = false, length = 4000)
  private String taskInput;

  @Column(updatable = false, length = 100)
  private String initiatedBy;

  @NotNull
  private LongTaskStatus status;

  @Column(length = 1000)
  private String message;

  private Integer progressPercent;

  @NotNull
  @Column(updatable = false)
  private Instant issuedAtUtc;

  private Instant startedAtUtc;

  private Instant doneAtUtc;

  @Column(length = 4000)
  private String errorDetails;

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTaskType() {
    return taskType;
  }

  public void setTaskType(String taskType) {
    this.taskType = taskType;
  }

  public String getTaskInput() {
    return taskInput;
  }

  public void setTaskInput(String taskInput) {
    this.taskInput = taskInput;
  }

  public String getInitiatedBy() {
    return initiatedBy;
  }

  public void setInitiatedBy(String initiatedBy) {
    this.initiatedBy = initiatedBy;
  }

  public LongTaskStatus getStatus() {
    return status;
  }

  public void setStatus(LongTaskStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getProgressPercent() {
    return progressPercent;
  }

  public void setProgressPercent(Integer progressPercent) {
    this.progressPercent = progressPercent;
  }

  public Instant getIssuedAtUtc() {
    return issuedAtUtc;
  }

  public void setIssuedAtUtc(Instant issuedAtUtc) {
    this.issuedAtUtc = issuedAtUtc;
  }

  public Instant getStartedAtUtc() {
    return startedAtUtc;
  }

  public void setStartedAtUtc(Instant startedAtUtc) {
    this.startedAtUtc = startedAtUtc;
  }

  public Instant getDoneAtUtc() {
    return doneAtUtc;
  }

  public void setDoneAtUtc(Instant doneAtUtc) {
    this.doneAtUtc = doneAtUtc;
  }

  public String getErrorDetails() {
    return errorDetails;
  }

  public void setErrorDetails(String errorDetails) {
    this.errorDetails = errorDetails;
  }
}
