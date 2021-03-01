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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.base.LruMap;
import com.appslandia.common.caching.AppCache;
import com.appslandia.common.utils.ObjectUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MemAppCache<K, V> implements AppCache<K, V> {

	final String name;

	final Map<Object, Object> cache;

	public MemAppCache(String name) {
		this(name, 32);
	}

	public MemAppCache(String name, int size) {
		this.name = name;
		this.cache = Collections.synchronizedMap(new LruMap<>(size));
	}

	public String getName() {
		return this.name;
	}

	@Override
	public V get(K key) {
		return ObjectUtils.cast(this.cache.get(key));
	}

	@Override
	public void put(K key, V value) {
		this.cache.put(key, value);
	}

	@Override
	public boolean putIfAbsent(K key, V value) {
		return this.cache.putIfAbsent(key, value) == null;
	}

	@Override
	public boolean containsKey(K key) {
		return this.cache.containsKey(key);
	}

	@Override
	public boolean remove(K key) {
		return this.cache.remove(key) != null;
	}

	@Override
	public boolean remove(K key, V oldValue) {
		return this.cache.remove(key, oldValue);
	}

	@Override
	public void removeAll(Set<? extends K> keys) {
		this.cache.keySet().removeAll(keys);
	}

	@Override
	public boolean replace(K key, V value) {
		return this.cache.replace(key, value) != null;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return this.cache.replace(key, oldValue, newValue);
	}

	@Override
	public void clear() {
		this.cache.clear();
	}
}
