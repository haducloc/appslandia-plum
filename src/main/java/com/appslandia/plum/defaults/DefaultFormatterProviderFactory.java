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

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.formatters.Formatter;
import com.appslandia.common.formatters.FormatterProvider;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultFormatterProviderFactory implements CDIFactory<FormatterProvider> {

	@Inject
	protected BeanManager beanManager;

	final BeanInstances beanInstances = new BeanInstances();

	@Produces
	@ApplicationScoped
	@Override
	public FormatterProvider produce() {
		final FormatterProvider impl = new FormatterProvider();

		CDIUtils.scanReferences(this.beanManager, Formatter.class, ReflectionUtils.EMPTY_ANNOTATIONS, MappedID.class, (mappedId, bi) -> {

			impl.addFormatter(mappedId.value(), bi.get());

			beanInstances.add(bi);
		});

		CDIUtils.scanSuppliers(this.beanManager, ReflectionUtils.EMPTY_ANNOTATIONS, Formatter.class, (bi) -> {

			Map<String, Formatter> m = ObjectUtils.cast(bi.get().get());

			for (Entry<String, Formatter> entry : m.entrySet()) {
				impl.addFormatter(entry.getKey(), entry.getValue());
			}

			beanInstances.add(bi);
		});
		return impl;
	}

	@Override
	public void dispose(@Disposes FormatterProvider impl) {
	}

	@PreDestroy
	public void dispose() {

		this.beanInstances.destroy();
	}
}
