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

import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializeException;
import com.appslandia.common.base.Mutex;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class ResourcesProvider {

    final Map<String, Resources> resourcesMap = new HashMap<>();

    final Mutex mutex = new Mutex();

    public Resources getResources(String language) throws InitializeException {
	Resources impl = this.resourcesMap.get(language);
	if (impl == null) {
	    synchronized (this.mutex) {
		impl = this.resourcesMap.get(language);
		if (impl == null) {

		    impl = loadResources(language);
		    this.resourcesMap.put(language, impl);
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

    protected abstract Resources loadResources(String language) throws InitializeException;
}
