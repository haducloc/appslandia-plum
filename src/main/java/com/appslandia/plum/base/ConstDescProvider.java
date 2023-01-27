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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.appslandia.common.base.ConstDesc;
import com.appslandia.common.base.InitializeException;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ConstDescProvider extends InitializeObject {

    private Map<ConstValue, String> constKeyMap = new HashMap<>();

    @Override
    protected void init() throws Exception {
	this.constKeyMap = Collections.unmodifiableMap(this.constKeyMap);
    }

    public String getDescKey(String constGroup, Object value) {
	this.initialize();
	return this.constKeyMap.get(new ConstValue(constGroup, value));
    }

    public void addConstClass(Class<?> constClass) {
	this.assertNotInitialized();
	try {
	    String defConstGroup = parseConstGroup(constClass);

	    for (Field field : constClass.getDeclaredFields()) {
		if (!ReflectionUtils.isPublicConst(field.getModifiers())) {
		    continue;
		}
		if (field.getDeclaredAnnotation(ConstDesc.class) == null) {
		    continue;
		}
		ConstDesc constDesc = field.getDeclaredAnnotation(ConstDesc.class);
		String constGroup = constDesc.value().isEmpty() ? defConstGroup : constDesc.value();

		// descKey: constGroup.constName
		String descKey = constGroup + "." + field.getName().toLowerCase(Locale.ENGLISH);
		this.constKeyMap.put(new ConstValue(constGroup, field.get(null)), descKey);
	    }
	} catch (Exception ex) {
	    throw new InitializeException(ex);
	}
    }

    public void addConst(String constGroup, Object value, String descKey) {
	this.constKeyMap.put(new ConstValue(constGroup, value), descKey);
    }

    private static String parseConstGroup(Class<?> constClass) {
	ConstDesc constDesc = constClass.getDeclaredAnnotation(ConstDesc.class);
	if ((constDesc != null) && !constDesc.value().isEmpty()) {
	    return constDesc.value();
	}
	return StringUtils.firstLowerCase(constClass.getSimpleName(), Locale.ENGLISH);
    }

    private static class ConstValue {

	final String constGroup;
	final Object value;

	public ConstValue(String constGroup, Object value) {
	    this.constGroup = constGroup;
	    this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
	    ConstValue another = (ConstValue) obj;
	    return Objects.equals(this.constGroup, another.constGroup) && Objects.equals(this.value, another.value);
	}

	@Override
	public int hashCode() {
	    int hash = 1, p = 31;
	    hash = p * hash + Objects.hashCode(this.constGroup);
	    hash = p * hash + Objects.hashCode(this.value);
	    return hash;
	}
    }
}
