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

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.base.InitializeException;
import com.appslandia.common.base.PropertyConfig;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.utils.StringFormat;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppConfigLoader;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultAppConfigFactory implements CDIFactory<AppConfig> {

	public static final String DEFAULT_CONFIG_PATH = "/WEB-INF/config.properties";
	public static final String ENV_CONFIG_PATH = "/WEB-INF/config.{}.properties";

	@Inject
	protected ServletContext servletContext;

	@Inject
	protected AppConfigLoader appConfigLoader;

	@Produces
	@ApplicationScoped
	@Override
	public AppConfig produce() {
		PropertyConfig config = new PropertyConfig();
		try {
			this.servletContext.log("Loading configs from " + DEFAULT_CONFIG_PATH);
			ServletUtils.loadProps(this.servletContext, DEFAULT_CONFIG_PATH, config);

			String envConfig = StringFormat.fmt(ENV_CONFIG_PATH, DeployEnv.getCurrent().getName());
			this.servletContext.log("Loading configs from " + envConfig);

			ServletUtils.loadProps(this.servletContext, envConfig, config);

		} catch (IOException ex) {
			throw new InitializeException(ex);
		}

		try {
			this.servletContext.log("Loading configs using appConfigLoader...");
			this.appConfigLoader.load(config);

		} catch (Exception ex) {
			throw new InitializeException(ex);
		}
		return new AppConfig(config);
	}

	@Override
	public void dispose(@Disposes AppConfig impl) {
	}
}
