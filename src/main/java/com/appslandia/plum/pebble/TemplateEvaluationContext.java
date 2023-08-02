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

package com.appslandia.plum.pebble;

import java.util.Map;
import java.util.stream.Collectors;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.jsp.TagUtils;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.el.ELProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@SuppressWarnings("unchecked")
public class TemplateEvaluationContext {

    final Map<String, Object> arguments;
    final PebbleTemplate template;
    final EvaluationContext evaluationContext;

    public TemplateEvaluationContext(Map<String, Object> arguments, PebbleTemplate template, EvaluationContext evaluationContext) {
	this.arguments = arguments;
	this.template = template;
	this.evaluationContext = evaluationContext;
    }

    public Map<String, Object> buildParameterMap() {
	return this.arguments.entrySet().stream().filter(entry -> TagUtils.isForParameter(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public HttpServletRequest getRequest() {
	return getRequiredVariable(PebbleUtils.VARIABLE_REQUEST);
    }

    public HttpServletResponse getResponse() {
	return getRequiredVariable(PebbleUtils.VARIABLE_RESPONSE);
    }

    public RequestContext getRequestContext() {
	return getRequiredVariable(PebbleUtils.VARIABLE_REQUEST_CONTEXT);
    }

    public ELProcessor getELProcessor() {
	return getRequiredVariable(PebbleUtils.VARIABLE_EL_PROCESSOR);
    }

    public ModelState getModelState() {
	return ServletUtils.getModelState(getRequest());
    }

    public <T> T getArgument(String name) {
	return (T) this.arguments.get(name);
    }

    public <T> T getArgument(String name, T defaultValue) {
	T value = (T) this.arguments.get(name);
	return (value != null) ? value : defaultValue;
    }

    public <T> T getRequiredArgument(String name) {
	return Asserts.notNull((T) this.arguments.get(name));
    }

    public <T> T getVariable(String name) {
	return (T) this.evaluationContext.getVariable(name);
    }

    public <T> T getRequiredVariable(String name) {
	return Asserts.notNull((T) this.evaluationContext.getVariable(name));
    }

    public Map<String, Object> getArguments() {
	return this.arguments;
    }

    public PebbleTemplate getTemplate() {
	return this.template;
    }

    public EvaluationContext getEvaluationContext() {
	return this.evaluationContext;
    }
}
