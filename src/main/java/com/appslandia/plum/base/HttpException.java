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

/**
 *
 * @author Loc Ha
 *
 */
public class HttpException extends RuntimeException implements ProblemSupport {
  private static final long serialVersionUID = 1L;

  final Problem problem;

  public HttpException(int status) {
    super();
    this.problem = new Problem().setStatus(status);
  }

  public HttpException(int status, String message) {
    super(message);
    this.problem = new Problem().setStatus(status).setTitle(message);
  }

  public HttpException(int status, String message, Throwable cause) {
    super(message, cause);
    this.problem = new Problem().setStatus(status).setTitle(message);
  }

  public HttpException(int status, Throwable cause) {
    super(cause);
    this.problem = new Problem().setStatus(status);
  }

  @Override
  public Problem getProblem() {
    return this.problem;
  }

  public HttpException setTitleKey(String titleKey) {
    this.problem.setTitleKey(titleKey);
    return this;
  }

  public HttpException setTitleKey(ResKey titleKey) {
    this.problem.setTitleKey(titleKey);
    return this;
  }

  public HttpException setDetailKey(String detailKey) {
    this.problem.setDetailKey(detailKey);
    return this;
  }

  public HttpException setDetailKey(ResKey detailKey) {
    this.problem.setDetailKey(detailKey);
    return this;
  }
}
