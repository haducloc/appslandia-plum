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

package com.appslandia.plum.mocks;

import java.util.List;
import java.util.stream.Collectors;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AuthHandler;
import com.appslandia.plum.base.AuthHandlerProvider;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MockAuthHandlerProvider extends AuthHandlerProvider {

    @Inject
    protected Instance<AuthHandler> handlerInstance;

    @Override
    protected void init() throws Exception {
	List<AuthHandler> handlers = this.handlerInstance.stream().collect(Collectors.toList());

	for (AuthHandler authHandler : handlers) {
	    MappedID mappedId = authHandler.getClass().getDeclaredAnnotation(MappedID.class);
	    Asserts.notNull(mappedId);

	    addAuthHandler(mappedId.value(), authHandler);
	}
	super.init();
    }
}
