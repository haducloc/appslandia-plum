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

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ResponseKeyBuilder {

	public static String getResponseKey(HttpServletRequest request, String cacheKey, boolean gzipContent) {
		final RequestContext ctx = ServletUtils.getRequestContext(request);

		StringBuilder key = format(cacheKey, (paramName) -> {
			switch (paramName) {

			case "language":
				return ctx.getLanguageId();
			case "controller":
				return ctx.getActionDesc().getController();
			case "action":
				return ctx.getActionDesc().getAction();
			default:
				return request.getParameter(paramName);
			}
		});
		if (gzipContent)
			key.append("#gzip");

		return key.toString();
	}

	private static final Pattern PARAM_HOLDER_PATTERN = Pattern.compile("\\{\\s*[a-z\\d._]+\\s*}", Pattern.CASE_INSENSITIVE);

	private static StringBuilder format(String str, Function<String, String> parameters) {
		StringBuilder out = new StringBuilder((int) (1.5 * str.length()));

		// {paramName}
		Matcher matcher = PARAM_HOLDER_PATTERN.matcher(str);

		int prevEnd = 0;
		while (matcher.find()) {

			// Non parameter
			if (prevEnd == 0) {
				out.append(str.substring(0, matcher.start()));
			} else {
				out.append(str.substring(prevEnd, matcher.start()));
			}

			// {paramName}
			String parameterGroup = matcher.group();
			String parameterName = parameterGroup.substring(1, parameterGroup.length() - 1).trim();
			String parameterValue = parameters.apply(parameterName);

			if (parameterValue == null) {
				out.append("null");

			} else {
				out.append(parameterValue.toString());
			}

			prevEnd = matcher.end();
		}
		if (prevEnd < str.length()) {
			out.append(str.substring(prevEnd));
		}

		return out;
	}
}
