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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.logging.AppLogger;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.StringFormat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class BeanInstanceContextListener {

    public static final String ATTRIBUTE_BEAN_INSTANCES = BeanInstanceContextListener.class.getName() + ".bean_instances";

    @Inject
    protected AppLogger appLogger;

    public void contextInitialized(@Observes @Initialized(ApplicationScoped.class) ServletContext sc) {
	sc.setAttribute(ATTRIBUTE_BEAN_INSTANCES, new ConcurrentHashMap<Class<?>, BeanInstance<?>>());
    }

    public void contextDestroyed(@Observes @Destroyed(ApplicationScoped.class) ServletContext sc) {
	this.appLogger.info("Destroying bean instances...");

	Map<Class<?>, BeanInstance<?>> map = ObjectUtils.cast(sc.getAttribute(ATTRIBUTE_BEAN_INSTANCES));
	if (map != null) {

	    map.values().stream().forEach(bi -> {
		try {
		    bi.destroy();

		} catch (RuntimeException ex) {
		    this.appLogger.error(ex);
		}
	    });
	}

	this.appLogger.info("Finished destroying bean instances.");
    }

    public static Map<Class<?>, BeanInstance<?>> getBeanInstances(ServletContext sc) {
	Map<Class<?>, BeanInstance<?>> map = ObjectUtils.cast(sc.getAttribute(ATTRIBUTE_BEAN_INSTANCES));
	if (map == null) {
	    throw new IllegalStateException(StringFormat.fmt("{} is required.", ATTRIBUTE_BEAN_INSTANCES));
	}
	return map;
    }
}
