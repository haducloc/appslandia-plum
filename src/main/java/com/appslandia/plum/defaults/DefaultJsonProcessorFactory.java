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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.json.bind.JsonbConfig;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.json.JsonbProcessor;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultJsonProcessorFactory implements CDIFactory<JsonProcessor> {

	@Produces
	@ApplicationScoped
	@Override
	public JsonProcessor produce() {
		JsonbConfig config = JsonbProcessor.newConfig();
		config.withFormatting(false);
		config.withNullValues(false);
		return new JsonbProcessor().setConfig(config);
	}

	@Override
	public void dispose(@Disposes JsonProcessor impl) {
		impl.destroy();
	}

	@Produces
	@ApplicationScoped
	@Json(Profile.COMPACT)
	public JsonProcessor produceCompact() {
		JsonbConfig config = JsonbProcessor.newConfig();
		config.withFormatting(false);
		config.withNullValues(false);
		return new JsonbProcessor().setConfig(config);
	}

	public void disposeCompact(@Disposes @Json(Profile.COMPACT) JsonProcessor impl) {
		impl.destroy();
	}

	@Produces
	@ApplicationScoped
	@Json(Profile.COMPACT_NULL)
	public JsonProcessor produceCompactNull() {
		JsonbConfig config = JsonbProcessor.newConfig();
		config.withFormatting(false);
		config.withNullValues(true);
		return new JsonbProcessor().setConfig(config);
	}

	public void disposeCompactNull(@Disposes @Json(Profile.COMPACT_NULL) JsonProcessor impl) {
		impl.destroy();
	}

	@Produces
	@ApplicationScoped
	@Json(Profile.PRETTY)
	public JsonProcessor producePretty() {
		JsonbConfig config = JsonbProcessor.newConfig();
		config.withFormatting(true);
		config.withNullValues(false);
		return new JsonbProcessor().setConfig(config);
	}

	public void disposePretty(@Disposes @Json(Profile.PRETTY) JsonProcessor impl) {
		impl.destroy();
	}

	@Produces
	@ApplicationScoped
	@Json(Profile.PRETTY_NULL)
	public JsonProcessor producePrettyNull() {
		JsonbConfig config = JsonbProcessor.newConfig();
		config.withFormatting(true);
		config.withNullValues(true);
		return new JsonbProcessor().setConfig(config);
	}

	public void disposePrettyNull(@Disposes @Json(Profile.PRETTY_NULL) JsonProcessor impl) {
		impl.destroy();
	}
}
