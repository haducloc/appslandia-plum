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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.AsyncListener;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.ArrayUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionDescUtils {

	public static boolean isActionResultOrVoid(Method actionMethod) {
		return ActionResult.class.isAssignableFrom(actionMethod.getReturnType()) || (actionMethod.getReturnType() == void.class);
	}

	public static boolean isActionMethod(Method method) {
		if (method.getDeclaringClass() == Object.class) {
			return false;
		}
		if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
			return false;
		}
		if (method.getDeclaredAnnotation(Removed.class) != null) {
			return false;
		}
		if (method.getDeclaredAnnotation(ChildAction.class) != null) {
			return false;
		}
		Out<Boolean> httpMethod = new Out<>();
		ActionDescProvider.parseAllowMethods(method, httpMethod);

		if (!Boolean.TRUE.equals(httpMethod.value)) {
			return false;
		}
		return true;
	}

	public static EnableFilters createEnableFilters(String[] filters) {
		return new EnableFilters() {

			final String[] _filters = ArrayUtils.copy(filters);

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableFilters.class;
			}

			@Override
			public String[] value() {
				return ArrayUtils.copy(this._filters);
			}
		};
	};

	public static ContentLang createContentLang(String policy) {
		return new ContentLang() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return ContentLang.class;
			}

			@Override
			public String value() {
				return policy;
			}

			@Override
			public boolean removed() {
				return false;
			}
		};
	};

	public static EnableAsync createEnableAsync(boolean markOnly, Class<? extends AsyncListener> asyncListener) {
		return new EnableAsync() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableAsync.class;
			}

			@Override
			public boolean markOnly() {
				return markOnly;
			}

			@Override
			public Class<? extends AsyncListener> asyncListener() {
				return asyncListener;
			}

			@Override
			public long timeoutMs() {
				return 0;
			}

			@Override
			public boolean removed() {
				return false;
			}
		};
	};

	public static Authorize createAuthorize(String[] roles, String[] policies, boolean appscope, boolean reauth) {
		return new Authorize() {

			final String[] _roles = ArrayUtils.copy(roles);
			final String[] _policies = ArrayUtils.copy(policies);

			@Override
			public Class<? extends Annotation> annotationType() {
				return Authorize.class;
			}

			@Override
			public String[] roles() {
				return ArrayUtils.copy(this._roles);
			}

			@Override
			public String[] policies() {
				return ArrayUtils.copy(this._policies);
			}

			@Override
			public boolean appscope() {
				return appscope;
			}

			@Override
			public boolean reauth() {
				return reauth;
			}

			@Override
			public boolean removed() {
				return false;
			}
		};
	};

	public static CacheControl createCacheControl(String policy) {
		return new CacheControl() {

			boolean nocache = CacheControl.NO_CACHE_POLICY.equals(policy);

			@Override
			public Class<? extends Annotation> annotationType() {
				return CacheControl.class;
			}

			@Override
			public String value() {
				return policy;
			}

			@Override
			public boolean nocache() {
				return this.nocache;
			}
		};
	};

	public static EnableCors createEnableCors(String policy) {
		return new EnableCors() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableCors.class;
			}

			@Override
			public String value() {
				return policy;
			}

			@Override
			public boolean removed() {
				return false;
			}
		};
	};

	public static EnableCache createEnableCache(String cacheName, String key) {
		return new EnableCache() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableCache.class;
			}

			@Override
			public String cacheName() {
				return cacheName;
			}

			@Override
			public String cacheKey() {
				return key;
			}
		};
	};

	public static EnableEtag createEnableEtag() {
		return new EnableEtag() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableEtag.class;
			}
		};
	};

	public static EnableGzip createEnableGzip() {
		return new EnableGzip() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableGzip.class;
			}

			@Override
			public boolean removed() {
				return false;
			}
		};
	};

	public static EnableJsonError createEnableJsonError() {
		return new EnableJsonError() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return EnableJsonError.class;
			}

			@Override
			public boolean removed() {
				return false;
			}
		};
	};

	public static ConsumeType createConsumeType(String contentType) {
		return new ConsumeType() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return ConsumeType.class;
			}

			@Override
			public String value() {
				return contentType;
			}
		};
	};
}
