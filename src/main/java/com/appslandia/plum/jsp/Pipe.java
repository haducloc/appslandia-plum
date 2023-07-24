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

package com.appslandia.plum.jsp;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.appslandia.common.base.GroupFormat;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.XmlEscaper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class Pipe {

    public abstract Object apply(Object value, String arg);

    private static Map<String, Pipe> PIPES = new HashMap<>();

    static {
	PIPES.put("upper", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("uppercase: value must be a string.");
		}
		return StringUtils.toUpperCase((String) value, Locale.ROOT);
	    }
	});
	PIPES.put("lower", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("lowercase: value must be a string.");
		}
		return StringUtils.toLowerCase((String) value, Locale.ROOT);
	    }
	});
	PIPES.put("trunc", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("trunc: value must be a string.");
		}
		if (arg == null) {
		    throw new IllegalArgumentException("trunc: length is required.");
		}
		int len = Integer.parseInt(arg);
		String s = (String) value;
		return (s.length() > len) ? s.substring(0, len) + "..." : s;
	    }
	});
	PIPES.put("fmtDate", new Pipe() {

	    final ConcurrentMap<String, DateTimeFormatter> formatters = new ConcurrentHashMap<>();

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (arg == null) {
		    throw new IllegalArgumentException("fmtDate: pattern is required.");
		}
		if (value instanceof Date) {
		    return new SimpleDateFormat(arg).format((Date) value);
		}
		if (value instanceof TemporalAccessor) {
		    return this.formatters.computeIfAbsent(arg, p -> DateTimeFormatter.ofPattern(p)).format((TemporalAccessor) value);
		}
		throw new IllegalArgumentException("fmtDate: value must be temporal.");
	    }
	});
	PIPES.put("fmtGroup", new Pipe() {

	    final ConcurrentMap<String, GroupFormat> formats = new ConcurrentHashMap<>();

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("fmtGroup: value must be a string.");
		}
		return this.formats.computeIfAbsent(arg, (f) -> new GroupFormat(f)).format((String) value);
	    }
	});
	PIPES.put("fmtNumber", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (!(value instanceof Number)) {
		    throw new IllegalArgumentException("fmtNumber: value must be a number.");
		}
		if (arg == null) {
		    throw new IllegalArgumentException("fmtNumber: format is required.");
		}
		return String.format(arg, value);
	    }
	});
	PIPES.put("maskStart", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("maskStart: value must be a string.");
		}
		if (arg == null) {
		    throw new IllegalArgumentException("maskStart: maskLength is required.");
		}
		int len = Integer.parseInt(arg);
		String s = (String) value;
		Asserts.isTrue(s.length() > len);

		char[] maskChars = new char[len];
		Arrays.fill(maskChars, '*');
		return new String(maskChars) + s.substring(len, s.length());
	    }
	});
	PIPES.put("maskEnd", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("maskEnd: value must be a string.");
		}
		if (arg == null) {
		    throw new IllegalArgumentException("maskEnd: maskLength is required.");
		}
		int len = Integer.parseInt(arg);
		String s = (String) value;
		Asserts.isTrue(s.length() > len);

		char[] maskChars = new char[len];
		Arrays.fill(maskChars, '*');
		return s.substring(0, s.length() - len) + new String(maskChars);
	    }
	});
	PIPES.put("toString", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (arg == null) {
		    return new ToStringBuilder().toString(value);
		}
		int level = Integer.parseInt(arg);
		return new ToStringBuilder(level).toString(value);
	    }
	});
	PIPES.put("asString", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value instanceof Iterable) {
		    return ObjectUtils.asString((Iterable<?>) value);
		}
		if (value.getClass().isArray()) {
		    return ObjectUtils.asString(value);
		}
		if (value instanceof Enumeration) {
		    return ObjectUtils.asString((Enumeration<?>) value);
		}
		throw new IllegalArgumentException("asString requires Iterable/Array/Enumeration parameter.");
	    }
	});
	PIPES.put("escCt", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("escCt: value must be a string.");
		}
		return XmlEscaper.escapeXmlContent((String) value);
	    }
	});
	PIPES.put("esc", new Pipe() {

	    @Override
	    public Object apply(Object value, String arg) {
		if (value == null) {
		    return null;
		}
		if (value.getClass() != String.class) {
		    throw new IllegalArgumentException("esc: value must be a string.");
		}
		return XmlEscaper.escapeXml((String) value);
	    }
	});
    }

    public static void register(String name, Pipe pipe) {
	PIPES.put(name, pipe);
    }

    private static final ConcurrentMap<String, PipeInfo[]> PIPE_INFOS = new ConcurrentHashMap<>();

    @Function(name = "pipe")
    public static String transform(Object value, String pipes) {
	Asserts.notNull(pipes);

	PipeInfo[] pis = PIPE_INFOS.computeIfAbsent(pipes, (c) -> parsePipes(c));
	for (PipeInfo pipe : pis) {

	    Pipe impl = PIPES.get(pipe.name);
	    Asserts.notNull(impl);

	    value = impl.apply(value, pipe.arg);
	}
	return ObjectUtils.toStringOrNull(value);
    }

    // pipe:param|another_pipe:param
    static PipeInfo[] parsePipes(String pipes) {
	int startIdx = 0;
	int endIdx;
	List<PipeInfo> list = new ArrayList<>(5);

	while ((endIdx = pipes.indexOf('|', startIdx)) != -1) {
	    String pipe = pipes.substring(startIdx, endIdx).trim();
	    if (!pipe.isEmpty()) {
		list.add(parsePipe(pipe));
	    }
	    startIdx = endIdx + 1;
	}
	if (startIdx < pipes.length()) {
	    String pipe = pipes.substring(startIdx).trim();
	    if (!pipe.isEmpty()) {
		list.add(parsePipe(pipe));
	    }
	}
	Asserts.hasElements(list, "No pipe provided.");
	return list.toArray(new PipeInfo[list.size()]);
    }

    static PipeInfo parsePipe(String pipe) {
	int idx = pipe.indexOf(':');
	if (idx < 0) {
	    return new PipeInfo(pipe, null);
	}
	return new PipeInfo(StringUtils.trimToNull(pipe.substring(0, idx)), StringUtils.trimToNull(pipe.substring(idx + 1)));
    }

    static class PipeInfo {
	final String name;
	final String arg;

	PipeInfo(String name, String arg) {
	    this.name = Asserts.notNull(name);
	    this.arg = arg;
	}
    }
}
