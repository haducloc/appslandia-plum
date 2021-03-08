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

package com.appslandia.plum.utils;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeSet;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.cdi.AnyLiteral;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.ObjectUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class DebugUtils {

	public static void writeBeanInfo(BeanManager manager, HttpServletResponse response) throws Exception {
		response.setContentType(MimeTypes.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		PrintWriter out = response.getWriter();
		TextBuilder sb = new TextBuilder();

		TreeSet<Bean<?>> sortedBeans = new TreeSet<>((b1, b2) -> b1.getBeanClass().getName().compareTo(b2.getBeanClass().getName()));
		sortedBeans.addAll(manager.getBeans(Object.class, AnyLiteral.IMPL));

		for (Bean<?> bean : sortedBeans) {
			sb.append(bean.getBeanClass()).append(", scope=").append(bean.getScope().getSimpleName()).append(", qualifiers=")
					.append(ObjectUtils.asString(bean.getQualifiers()));
			sb.appendln();
		}
		out.write(sb.toString());
		out.flush();
	}

	public static void writeServletInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(MimeTypes.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		PrintWriter out = response.getWriter();

		for (ServletRegistration registration : request.getServletContext().getServletRegistrations().values()) {
			out.append("servletName=").append(registration.getName()).append(", servletClass=").append(registration.getClassName()).append(", urlPatterns=")
					.append(ObjectUtils.asString(registration.getMappings()));
			out.println();
		}
		out.println();

		for (FilterRegistration registration : request.getServletContext().getFilterRegistrations().values()) {
			out.append("filterName=").append(registration.getName()).append(", filterClass=").append(registration.getClassName()).append(", urlPatterns=")
					.append(ObjectUtils.asString(registration.getUrlPatternMappings())).append(", servletNames=")
					.append(ObjectUtils.asString(registration.getServletNameMappings()));
			out.println();
		}
		out.flush();
	}

	public static void writeTcpInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(MimeTypes.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		PrintWriter out = response.getWriter();

		try {
			out.append("InetAddress.getLocalHost(): ").append(InetAddress.getLocalHost().toString());
			out.println();
		} catch (Exception ex) {
			out.append("InetAddress.getLocalHost(): ").append(ex.getMessage());
			out.println();
		}
		out.println();

		out.append("request.getServerName(): ").append(request.getServerName());
		out.println();
		out.append("request.getServerPort(): ").append(String.valueOf(request.getServerPort()));
		out.println();
		out.append("request.getLocalName(): ").append(request.getLocalName());
		out.println();
		out.append("request.getLocalPort(): ").append(String.valueOf(request.getLocalPort()));
		out.println();
		out.append("request.getRemoteHost(): ").append(request.getRemoteHost());
		out.println();
		out.append("request.getRemotePort(): ").append(String.valueOf(request.getRemotePort()));
		out.println();
		out.append("request.getRemoteAddr(): ").append(request.getRemoteAddr());
		out.println();

		out.flush();
	}

	public static void writeEnvsProps(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(MimeTypes.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		PrintWriter out = response.getWriter();

		for (Map.Entry<String, String> env : System.getenv().entrySet()) {
			out.append(env.getKey()).append("=").append(env.getValue());
			out.println();
		}

		out.println();
		for (Map.Entry<Object, Object> env : System.getProperties().entrySet()) {
			out.append(env.getKey().toString()).append("=").append(env.getValue().toString());
			out.println();
		}

		out.flush();
	}

	public static void writeZoneInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType(MimeTypes.TEXT_PLAIN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		PrintWriter out = response.getWriter();

		out.append("ZoneId.systemDefault(): ").append(ZoneId.systemDefault().toString());
		out.println();
		out.append("OffsetTime.now().getOffset().getId(): ").append(OffsetTime.now().getOffset().getId());

		out.println();
		out.println();

		for (String zoneId : ZoneId.getAvailableZoneIds()) {
			out.append(zoneId);
			out.println();
		}

		out.flush();
	}
}
