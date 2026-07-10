// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

/**
 *
 * @author Loc Ha
 *
 */
public class PagerItem {

  enum ItemType {
    INDEX, DOT, PREV, NEXT
  }

  final ItemType type;
  final int index;

  public PagerItem(ItemType type, int index) {
    this.type = type;
    this.index = index;
  }

  public ItemType getType() {
    return type;
  }

  public int getIndex() {
    return index;
  }

  public boolean isPageType() {
    return type != ItemType.DOT;
  }

  public boolean isIndexType() {
    return type == ItemType.INDEX;
  }

  public boolean isDotType() {
    return type == ItemType.DOT;
  }

  public boolean isPrevType() {
    return type == ItemType.PREV;
  }

  public boolean isNextType() {
    return type == ItemType.NEXT;
  }

  @Override
  public int hashCode() {
    int hash = 1, p = 31;
    hash = p * hash + type.hashCode();
    hash = p * hash + index;
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
    return (type == that.type) && (index == that.index);
  }
}
