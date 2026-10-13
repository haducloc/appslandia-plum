// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.appslandia.common.base.InitializingException;
import com.appslandia.common.base.Language;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ResourcesProvider {

  private final ConcurrentMap<String, ConcurrentMap<Language, Resources>> resourcesMap = new ConcurrentHashMap<>();

  public Resources getResources(String module, Language language) throws InitializingException {
    var resMap = resourcesMap.computeIfAbsent(module, m -> new ConcurrentHashMap<>());
    return resMap.computeIfAbsent(language, l -> loadResources(module, l));
  }

  public void clearResources(String module) {
    resourcesMap.remove(module);
  }

  public void clearAllResources() {
    resourcesMap.clear();
  }

  protected abstract Resources loadResources(String module, Language language) throws InitializingException;

  protected static class ResourceBundleImpl extends ResourceBundle {

    final Map<String, Object> lookup;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ResourceBundleImpl(Properties properties) {
      lookup = new HashMap(properties);
    }

    public ResourceBundleImpl(Map<String, Object> lookup) {
      this.lookup = lookup;
    }

    @Override
    public Object handleGetObject(String key) {
      if (key == null) {
        throw new NullPointerException();
      }
      return lookup.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
      var parent = this.parent;
      return new ResourceBundleEnumeration(lookup.keySet(), (parent != null) ? parent.getKeys() : null);
    }

    @Override
    protected Set<String> handleKeySet() {
      return lookup.keySet();
    }
  }

  protected static class ResourceBundleEnumeration implements Enumeration<String> {

    final Set<String> keys;
    final Iterator<String> iter;
    final Enumeration<String> pKeys;

    private String next = null;

    public ResourceBundleEnumeration(Set<String> keys, Enumeration<String> pKeys) {
      this.keys = keys;
      iter = keys.iterator();
      this.pKeys = pKeys;
    }

    @Override
    public boolean hasMoreElements() {
      if (next == null) {

        if (iter.hasNext()) {
          next = iter.next();

        } else if (pKeys != null) {

          while (next == null && pKeys.hasMoreElements()) {
            next = pKeys.nextElement();

            if (keys.contains(next)) {
              next = null;
            }
          }
        }
      }
      return next != null;
    }

    @Override
    public String nextElement() {
      if (hasMoreElements()) {
        var result = next;
        next = null;
        return result;
      } else {
        throw new NoSuchElementException();
      }
    }
  }
}
