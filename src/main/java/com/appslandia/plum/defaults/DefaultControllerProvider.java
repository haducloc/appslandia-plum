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

package com.appslandia.plum.defaults;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.plum.base.ControllerProvider;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultControllerProvider implements ControllerProvider {

	@Inject
	protected BeanManager beanManager;

	@Inject
	protected ServletContext servletContext;

	final Map<Class<?>, BeanInstance<?>> controllers = new HashMap<>();

	final Object mutex = new Object();

	@Override
	public Object getController(Class<?> controllerClass) throws Exception {
		BeanInstance<?> inst = this.controllers.get(controllerClass);
		if (inst == null) {

			synchronized (this.mutex) {
				if ((inst = this.controllers.get(controllerClass)) == null) {

					inst = CDIUtils.getReference(this.beanManager, controllerClass);
					this.controllers.put(controllerClass, inst);
				}
			}
		}
		return inst.get();
	}

	@PreDestroy
	public void dispose() {
		this.servletContext.log("Destroying controller instances...");

		this.controllers.values().stream().forEach(bi -> {
			try {
				bi.destroy();

			} catch (RuntimeException ex) {
				this.servletContext.log(ex.getMessage(), ex);
			}
		});

		this.servletContext.log("Finished destroying controller instances.");
	}
}
