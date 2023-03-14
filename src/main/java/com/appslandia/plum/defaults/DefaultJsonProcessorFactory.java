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

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.jose.JoseJsonb;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.json.JsonbProcessor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.JsonbConfig;

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
	return createJsonbProcessor(true, true);
    }

    @Override
    public void dispose(@Disposes JsonProcessor impl) {
	impl.destroy();
    }

    @Produces
    @ApplicationScoped
    @Json(Profile.COMPACT)
    public JsonProcessor produceCompact() {
	return createJsonbProcessor(false, true);
    }

    public void disposeCompact(@Disposes @Json(Profile.COMPACT) JsonProcessor impl) {
	impl.destroy();
    }

    @Produces
    @ApplicationScoped
    @Json(Profile.PRETTY)
    public JsonProcessor producePretty() {
	return createJsonbProcessor(true, true);
    }

    public void disposePretty(@Disposes @Json(Profile.PRETTY) JsonProcessor impl) {
	impl.destroy();
    }

    static JsonbProcessor createJsonbProcessor(boolean formatting, boolean serializeNulls) {
	JsonbConfig config = JoseJsonb.newJsonbConfig();
	config.withFormatting(formatting);
	config.withNullValues(serializeNulls);

	return new JsonbProcessor().setConfig(config);
    }
}
