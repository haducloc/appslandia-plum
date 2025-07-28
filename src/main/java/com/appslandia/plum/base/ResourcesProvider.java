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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import com.appslandia.common.base.InitializeException;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ResourcesProvider {

  final Map<Locale, Resources> resourcesMap = new HashMap<>();
  final Object mutex = new Object();

  public Resources getResources(Locale locale) throws InitializeException {
    var impl = this.resourcesMap.get(locale);
    if (impl == null) {
      synchronized (this.mutex) {
        impl = this.resourcesMap.get(locale);
        if (impl == null) {

          impl = loadResources(locale);
          this.resourcesMap.put(locale, impl);
        }
      }
    }
    return impl;
  }

  public void clearResources() {
    synchronized (this.mutex) {
      this.resourcesMap.clear();
    }
  }

  protected abstract Resources loadResources(Locale locale) throws InitializeException;

  protected static class ResourceBundleImpl extends ResourceBundle {

    final Map<String, Object> lookup;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ResourceBundleImpl(Properties properties) {
      this.lookup = new HashMap(properties);
    }

    public ResourceBundleImpl(Map<String, Object> lookup) {
      this.lookup = lookup;
    }

    @Override
    public Object handleGetObject(String key) {
      if (key == null) {
        throw new NullPointerException();
      }
      return this.lookup.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
      var parent = this.parent;
      return new ResourceBundleEnumeration(this.lookup.keySet(), (parent != null) ? parent.getKeys() : null);
    }

    @Override
    protected Set<String> handleKeySet() {
      return this.lookup.keySet();
    }
  }

  protected static class ResourceBundleEnumeration implements Enumeration<String> {

    final Set<String> keys;
    final Iterator<String> iter;
    final Enumeration<String> pKeys;

    private String next = null;

    public ResourceBundleEnumeration(Set<String> keys, Enumeration<String> pKeys) {
      this.keys = keys;
      this.iter = keys.iterator();
      this.pKeys = pKeys;
    }

    @Override
    public boolean hasMoreElements() {
      if (this.next == null) {

        if (this.iter.hasNext()) {
          this.next = this.iter.next();

        } else if (this.pKeys != null) {

          while (this.next == null && this.pKeys.hasMoreElements()) {
            this.next = this.pKeys.nextElement();

            if (this.keys.contains(this.next)) {
              this.next = null;
            }
          }
        }
      }
      return this.next != null;
    }

    @Override
    public String nextElement() {
      if (this.hasMoreElements()) {
        var result = this.next;
        this.next = null;
        return result;
      } else {
        throw new NoSuchElementException();
      }
    }
  }
}
