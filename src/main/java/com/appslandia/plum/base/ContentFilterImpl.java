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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.caching.AppCache;
import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
@MappedID(ContentFilterImpl.NAME)
public class ContentFilterImpl implements ActionFilter {

	public static final String NAME = "contentFilter";

	@Inject
	protected AppConfig appConfig;

	@Inject
	protected AppCacheManager appCacheManager;

	@Inject
	protected ResponseValueBuilder responseValueBuilder;

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext, ActionFilterChain filterChain)
			throws Exception {
		// GZIP
		final boolean gzipContent = (requestContext.getActionDesc().getEnableGzip() != null) && ServletUtils.isGzipAccepted(request);

		// Cache
		if ((requestContext.getActionDesc().getEnableCache() != null) && requestContext.isGetOrHead()) {

			EnableCache enableCache = requestContext.getActionDesc().getEnableCache();
			String responseKey = ResponseKeyBuilder.getResponseKey(request, enableCache.cacheKey(), gzipContent);

			// responseValue
			AppCache<String, ResponseValue> cache = this.appCacheManager.getRequiredCache(enableCache.cacheName());
			ResponseValue responseValue = cache.get(responseKey);
			if (responseValue == null) {

				// VARY: Accept-Encoding
				if (requestContext.getActionDesc().getEnableGzip() != null) {
					response.addHeader("Vary", "Accept-Encoding");
				}

				ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true, gzipContent);
				filterChain.doFilter(request, wrapper, requestContext);

				if ((300 <= wrapper.getStatus()) && (wrapper.getStatus() < 400)) {
					return;
				}
				wrapper.finishWrapper();

				ResponseWrapperImpl respImpl = ServletUtils.unwrapResponse(response, ResponseWrapperImpl.class);
				String etag = (requestContext.getActionDesc().getEnableEtag() != null) ? ServletUtils.toEtag(wrapper.getContent().digest("MD5")) : null;
				responseValue = this.responseValueBuilder.build(wrapper.getContent(), respImpl.toHeaderValues(), etag);
				cache.putIfAbsent(responseKey, responseValue);

			} else {
				responseValue.writeHeaders(response);
			}

			if ((requestContext.getActionDesc().getEnableEtag() == null) || !ServletUtils.checkNotModified(request, response, responseValue.getEtag())) {
				response.setContentLengthLong(responseValue.size());
				responseValue.writeTo(response.getOutputStream());
			}
			return;
		}

		// VARY: Accept-Encoding
		if (requestContext.getActionDesc().getEnableGzip() != null) {
			response.addHeader("Vary", "Accept-Encoding");
		}

		// ETAG
		if ((requestContext.getActionDesc().getEnableEtag() != null) && requestContext.isGetOrHead()) {
			ContentResponseWrapper wrapper = new ContentResponseWrapper(response, true, gzipContent);
			filterChain.doFilter(request, wrapper, requestContext);

			if ((300 <= wrapper.getStatus()) && (wrapper.getStatus() < 400)) {
				return;
			}
			wrapper.finishWrapper();

			if (!ServletUtils.checkNotModified(request, response, ServletUtils.toEtag(wrapper.getContent().digest("MD5")))) {
				response.setContentLengthLong(wrapper.getContent().size());
				wrapper.getContent().writeTo(response.getOutputStream());
			}
			return;
		}

		// GZIP
		if (gzipContent) {

			// Transfer-Encoding: CHUNKED
			GzipResponseWrapper wrapper = new GzipResponseWrapper(response);
			try {
				filterChain.doFilter(request, wrapper, requestContext);

				if ((300 <= wrapper.getStatus()) && (wrapper.getStatus() < 400)) {
					return;
				}
				wrapper.finishWrapper();

			} catch (Exception ex) {
				if (wrapper.isCommitted()) {
					wrapper.finishWrapper();
				}
				if (ex instanceof Exception) {
					throw (Exception) ex;
				} else {
					throw new ServletException(ex);
				}
			}
			return;
		}

		// Others
		filterChain.doFilter(request, response, requestContext);
	}
}
