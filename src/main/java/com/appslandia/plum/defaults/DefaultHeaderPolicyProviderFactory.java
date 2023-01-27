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

import com.appslandia.common.base.MappedID;
import com.appslandia.common.cdi.BeanInstances;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.CDIUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.plum.base.HeaderPolicy;
import com.appslandia.plum.base.HeaderPolicyProvider;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultHeaderPolicyProviderFactory implements CDIFactory<HeaderPolicyProvider> {

    @Inject
    protected BeanManager beanManager;

    final BeanInstances beanInstances = new BeanInstances();

    @Produces
    @ApplicationScoped
    @Override
    public HeaderPolicyProvider produce() {
	final HeaderPolicyProvider impl = new HeaderPolicyProvider();

	CDIUtils.scanReferences(this.beanManager, HeaderPolicy.class, ReflectionUtils.EMPTY_ANNOTATIONS, MappedID.class, (mappedId, bi) -> {

	    impl.addHeaderPolicy(mappedId.value(), bi.get());

	    beanInstances.add(bi);
	});

	// @Supplier(HeaderPolicy.class)
	CDIUtils.scanSuppliers(this.beanManager, ReflectionUtils.EMPTY_ANNOTATIONS, HeaderPolicy.class, (bi) -> {

	    Map<String, HeaderPolicy> m = ObjectUtils.cast(bi.get().get());

	    for (Entry<String, HeaderPolicy> entry : m.entrySet()) {
		impl.addHeaderPolicy(entry.getKey(), entry.getValue());
	    }

	    beanInstances.add(bi);
	});
	return impl;
    }

    @Override
    public void dispose(@Disposes HeaderPolicyProvider impl) {
    }

    @PreDestroy
    public void dispose() {

	this.beanInstances.destroy();
    }
}
