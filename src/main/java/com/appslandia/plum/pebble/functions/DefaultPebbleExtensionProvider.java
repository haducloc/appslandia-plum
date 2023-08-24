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

package com.appslandia.plum.pebble.functions;

import java.util.HashMap;
import java.util.Map;

import com.appslandia.plum.pebble.PebbleExtensionProvider;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.extension.Test;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultPebbleExtensionProvider extends PebbleExtensionProvider {

    @Override
    public Map<String, Function> getFunctions() {
	Map<String, Function> impls = new HashMap<>();

	impls.put("label", new LabelFunction());
	impls.put("input", new InputFunction());
	impls.put("checkbox", new CheckboxFunction());
	impls.put("radio", new RadioFunction());
	impls.put("textarea", new TextAreaFunction());

	impls.put("select", new SelectFunction());
	impls.put("selectItems", new SelectItemsFunction());
	impls.put("datalist", new DatalistFunction());

	impls.put("hiddenCheckbox", new HiddenCheckboxFunction());
	impls.put("hiddenRadio", new HiddenRadioFunction());
	impls.put("hiddenSelect", new HiddenSelectFunction());

	impls.put("fieldClass", new FieldClassFunction());
	impls.put("fieldValue", new FieldValueFunction());
	impls.put("fieldError", new FieldErrorFunction());

	impls.put("messages", new MessagesFunction());
	impls.put("formErrors", new FormErrorsFunction());

	impls.put("userDName", new UserDNameFunction());
	impls.put("actionUrl", new ActionUrlFunction());

	impls.put("mailto", new MailtoFunction());
	impls.put("symbol", new SymbolFunction());

	impls.put("deployEnv", new DeployEnvFunction());
	impls.put("nowMs", new NowMsFunction());
	return impls;
    }

    @Override
    public Map<String, Test> getTests() {
	Map<String, Test> impls = new HashMap<>();

	impls.put("env", new IsEnvTest());
	return impls;
    }
}
