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

package com.appslandia.plum.base;

import java.util.Map;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.Language;
import com.appslandia.common.formatters.Formatter;
import com.appslandia.common.formatters.FormatterProvider;
import com.appslandia.common.utils.StringFormat;
import com.appslandia.plum.utils.XmlEscaper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestContext {

	public static final String REQUEST_ATTRIBUTE_ID = "ctx";

	private boolean pathLanguage;
	private FormatProvider formatProvider;
	private Resources resources;
	private FormatterProvider formatterProvider;

	private String requestUrl;
	private boolean getOrHead;
	private ActionDesc actionDesc;
	private Map<String, String> pathParamMap;

	private String clientId;
	private String module;
	private boolean localhost;

	public RequestContext createRequestContext(ActionDesc actionDesc) {
		RequestContext context = new RequestContext();
		context.pathLanguage = this.pathLanguage;
		context.formatProvider = this.formatProvider;
		context.resources = this.resources;
		context.formatterProvider = this.formatterProvider;

		context.requestUrl = this.requestUrl;
		context.getOrHead = this.getOrHead;
		context.actionDesc = actionDesc;

		context.clientId = this.clientId;
		context.module = this.module;
		context.localhost = this.localhost;
		return context;
	}

	public boolean isRoute(String controller) {
		if (this.actionDesc == null) {
			return false;
		}
		return this.actionDesc.getController().equalsIgnoreCase(controller);
	}

	public boolean isRoute(String controller, String action) {
		if (this.actionDesc == null) {
			return false;
		}
		return this.actionDesc.getAction().equalsIgnoreCase(action) && this.actionDesc.getController().equalsIgnoreCase(controller);
	}

	public String getLanguageId() {
		return this.formatProvider.getLanguage().getId();
	}

	public Language getLanguage() {
		return this.formatProvider.getLanguage();
	}

	public boolean isPathLanguage() {
		return this.pathLanguage;
	}

	protected void setPathLanguage(boolean pathLanguage) {
		this.pathLanguage = pathLanguage;
	}

	public FormatProvider getFormatProvider() {
		return this.formatProvider;
	}

	protected void setFormatProvider(FormatProvider formatProvider) {
		this.formatProvider = formatProvider;
	}

	public Resources getResources() {
		return this.resources;
	}

	protected void setResources(Resources resources) {
		this.resources = resources;
	}

	public FormatterProvider getFormatterProvider() {
		return this.formatterProvider;
	}

	protected void setFormatterProvider(FormatterProvider formatterProvider) {
		this.formatterProvider = formatterProvider;
	}

	public String getRequestUrl() {
		return this.requestUrl;
	}

	protected void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public boolean isGetOrHead() {
		return this.getOrHead;
	}

	protected void setGetOrHead(boolean getOrHead) {
		this.getOrHead = getOrHead;
	}

	public ActionDesc getActionDesc() {
		return this.actionDesc;
	}

	protected void setActionDesc(ActionDesc actionDesc) {
		this.actionDesc = actionDesc;
	}

	public Map<String, String> getPathParamMap() {
		return this.pathParamMap;
	}

	protected void setPathParamMap(Map<String, String> pathParamMap) {
		this.pathParamMap = pathParamMap;
	}

	public String getClientId() {
		return this.clientId;
	}

	protected void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getModule() {
		return this.module;
	}

	protected void setModule(String module) {
		this.module = module;
	}

	public boolean isLocalhost() {
		return this.localhost;
	}

	protected void setLocalhost(boolean localhost) {
		this.localhost = localhost;
	}

	public String res(String key) {
		return this.resources.get(key);
	}

	public String res(String key, Object p1) {
		return this.resources.get(key, p1);
	}

	public String res(String key, Object p1, Object p2) {
		return this.resources.get(key, p1, p2);
	}

	public String res(String key, Object p1, Object p2, Object p3) {
		return this.resources.get(key, p1, p2, p3);
	}

	public String esc(String key) {
		return XmlEscaper.escapeXml(this.resources.get(key));
	}

	public String esc(String key, Object p1) {
		return XmlEscaper.escapeXml(this.resources.get(key, p1));
	}

	public String esc(String key, Object p1, Object p2) {
		return XmlEscaper.escapeXml(this.resources.get(key, p1, p2));
	}

	public String esc(String key, Object p1, Object p2, Object p3) {
		return XmlEscaper.escapeXml(this.resources.get(key, p1, p2, p3));
	}

	public String ifEsc(boolean b, String trueKey) {
		if (b) {
			return XmlEscaper.escapeXml(this.resources.get(trueKey));
		}
		return null;
	}

	public String ifEsc(boolean b, String trueKey, Object p1) {
		if (b) {
			return XmlEscaper.escapeXml(this.resources.get(trueKey, p1));
		}
		return null;
	}

	public String ifEsc(boolean b, String trueKey, Object p1, Object p2) {
		if (b) {
			return XmlEscaper.escapeXml(this.resources.get(trueKey, p1, p2));
		}
		return null;
	}

	public String ifEsc(boolean b, String trueKey, Object p1, Object p2, Object p3) {
		if (b) {
			return XmlEscaper.escapeXml(this.resources.get(trueKey, p1, p2, p3));
		}
		return null;
	}

	public String iifEsc(boolean b, String trueKey, String falseKey) {
		if (b) {
			return XmlEscaper.escapeXml(this.resources.get(trueKey));
		}
		return XmlEscaper.escapeXml(this.resources.get(falseKey));
	}

	public String escCt(String key) {
		return XmlEscaper.escapeXmlContent(this.resources.get(key));
	}

	public String escCt(String key, Object p1) {
		return XmlEscaper.escapeXmlContent(this.resources.get(key, p1));
	}

	public String escCt(String key, Object p1, Object p2) {
		return XmlEscaper.escapeXmlContent(this.resources.get(key, p1, p2));
	}

	public String escCt(String key, Object p1, Object p2, Object p3) {
		return XmlEscaper.escapeXmlContent(this.resources.get(key, p1, p2, p3));
	}

	public String ifEscCt(boolean b, String trueKey) {
		if (b) {
			return XmlEscaper.escapeXmlContent(this.resources.get(trueKey));
		}
		return null;
	}

	public String ifEscCt(boolean b, String trueKey, Object p1) {
		if (b) {
			return XmlEscaper.escapeXmlContent(this.resources.get(trueKey, p1));
		}
		return null;
	}

	public String ifEscCt(boolean b, String trueKey, Object p1, Object p2) {
		if (b) {
			return XmlEscaper.escapeXmlContent(this.resources.get(trueKey, p1, p2));
		}
		return null;
	}

	public String ifEscCt(boolean b, String trueKey, Object p1, Object p2, Object p3) {
		if (b) {
			return XmlEscaper.escapeXmlContent(this.resources.get(trueKey, p1, p2, p3));
		}
		return null;
	}

	public String iifEscCt(boolean b, String trueKey, String falseKey) {
		if (b) {
			return XmlEscaper.escapeXmlContent(this.resources.get(trueKey));
		}
		return XmlEscaper.escapeXmlContent(this.resources.get(falseKey));
	}

	public String fmtVal(Object value) {
		if (value == null) {
			return null;
		}
		return fmtVal(value, null);
	}

	public String fmtVal(Object value, String formatterId) {
		if (value == null) {
			return null;
		}
		Formatter fmt = this.formatterProvider.findFormatter(formatterId, value.getClass());
		if (fmt == null) {
			return value.toString();
		}
		return fmt.format(value, this.formatProvider);
	}

	public String fmtValEsc(Object value, String formatterId) {
		if (value == null) {
			return null;
		}
		return XmlEscaper.escapeXml(fmtVal(value, formatterId));
	}

	public String fmtValEscCt(Object value, String formatterId) {
		if (value == null) {
			return null;
		}
		return XmlEscaper.escapeXmlContent(fmtVal(value, formatterId));
	}

	public String fmtEscCt(String key, Object p1) {
		String msg = XmlEscaper.escapeXmlContent(this.resources.get(key));
		return StringFormat.format(msg, p1);
	}

	public String fmtEscCt(String key, Object p1, Object p2) {
		String msg = XmlEscaper.escapeXmlContent(this.resources.get(key));
		return StringFormat.format(msg, p1, p2);
	}

	public String fmtEscCt(String key, Object p1, Object p2, Object p3) {
		String msg = XmlEscaper.escapeXmlContent(this.resources.get(key));
		return StringFormat.format(msg, p1, p2, p3);
	}
}
