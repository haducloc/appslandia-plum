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

import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.appslandia.plum.base.ActionScanner;
import com.appslandia.plum.base.DynFilterRegister;
import com.appslandia.plum.base.DynMultipartConfig;
import com.appslandia.plum.base.DynServletRegister;
import com.appslandia.plum.base.EnableAsync;
import com.appslandia.plum.base.EnableParts;
import com.appslandia.plum.base.ExecutorHandler;
import com.appslandia.plum.base.InitializerHandler;
import com.appslandia.plum.base.Startup;
import com.appslandia.plum.base.StartupConfig;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@StartupConfig(priority = StartupConfig.PRIORITY_LIBRARY_BEFORE + 500)
public class DefaultDynHandlerInitializer implements Startup {

	@Override
	public void onStartup(ServletContext sc, List<Class<? extends Startup>> startupClasses) throws ServletException {
		ActionScanner scanner = ActionScanner.getInstance();

		String[] urlMappings = { "", "/" };
		boolean asyncSupported = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableAsync.class) != null);
		boolean partsSupported = scanner.hasAction(m -> m.getDeclaredAnnotation(EnableParts.class) != null);
		DynMultipartConfig multipartConfig = partsSupported ? buildMultipartConfig() : null;

		String executorName = ExecutorHandler.class.getSimpleName();
		String initializerName = InitializerHandler.class.getSimpleName();

		new DynServletRegister().servletName(executorName).servletClass(ExecutorHandler.class).urlPatterns(urlMappings).asyncSupported(asyncSupported)
				.multipartConfig(multipartConfig).registerTo(sc);
		new DynFilterRegister().filterName(initializerName).filterClass(InitializerHandler.class).servletNames(executorName).asyncSupported(asyncSupported)
				.dispatcherTypes(DispatcherType.REQUEST).registerTo(sc);
	}

	protected DynMultipartConfig buildMultipartConfig() {
		return new DynMultipartConfig();
	}
}
