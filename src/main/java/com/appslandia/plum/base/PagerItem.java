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
public class PagerItem {

  static final int TYPE_INDEX = 1;
  static final int TYPE_DOT = 2;
  static final int TYPE_PREV = 3;
  static final int TYPE_NEXT = 4;

  private int type;
  private int index;

  public PagerItem(int type, int index) {
    this.type = type;
    this.index = index;
  }

  public int getType() {
    return this.type;
  }

  public int getIndex() {
    return this.index;
  }

  public boolean isIndexType() {
    return this.type == TYPE_INDEX;
  }

  public boolean isDotType() {
    return this.type == TYPE_DOT;
  }

  public boolean isPrevType() {
    return this.type == TYPE_PREV;
  }

  public boolean isNextType() {
    return this.type == TYPE_NEXT;
  }

  @Override
  public int hashCode() {
    int hash = 1, p = 31;
    hash = p * hash + this.type;
    hash = p * hash + this.index;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PagerItem that)) {
      return false;
    }
    return (this.type == that.type) && (this.index == that.index);
  }
}
