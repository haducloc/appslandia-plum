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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HeaderPolicyProvider extends InitializeObject {

	private Map<String, HeaderPolicy> headerPolicyMap = new HashMap<>();

	@Override
	protected void init() throws Exception {
		if (!this.headerPolicyMap.containsKey(CacheControl.NO_CACHE_POLICY)) {
			this.headerPolicyMap.put(CacheControl.NO_CACHE_POLICY, new HeaderPolicy() {
				final String noCache = new CacheControlBuilder().maxAge(0, TimeUnit.SECONDS).noCache().noStore().mustRevalidate().toString();

				@Override
				public void writePolicy(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) {
					response.setHeader("Cache-Control", this.noCache);
					response.setHeader("Expires", "0");
					response.setHeader("Pragma", "no-cache");
				}
			});
		}
		if (!this.headerPolicyMap.containsKey(ContentLang.DEFAULT_POLICY)) {
			this.headerPolicyMap.put(ContentLang.DEFAULT_POLICY, (request, response, requestContext) -> {
				response.setHeader("Content-Language", ServletUtils.getAppScoped(request, LanguageProvider.class).getLanguageIds());
			});
		}
		this.headerPolicyMap = Collections.unmodifiableMap(this.headerPolicyMap);
	}

	public Map<String, HeaderPolicy> getHeaderPolicyMap() {
		this.initialize();
		return this.headerPolicyMap;
	}

	public HeaderPolicy getHeaderPolicy(String name) throws IllegalArgumentException {
		this.initialize();
		HeaderPolicy impl = this.headerPolicyMap.get(name);
		if (impl == null) {
			throw new IllegalArgumentException("headerPolicy is required (name=" + name + ")");
		}
		return impl;
	}

	public void addHeaderPolicy(String name, HeaderPolicy impl) {
		this.assertNotInitialized();
		AssertUtils.assertTrue(!this.headerPolicyMap.containsKey(name), "headerPolicy already exists (name=" + name + ")");
		this.headerPolicyMap.put(name, impl);
	}
}
