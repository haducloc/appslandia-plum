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

package com.appslandia.plum.jsp;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TagTldGenerator {

    public static void main(String[] args) throws Exception {
	TextBuilder sb = new TextBuilder();

	generateTag(FormTag.class, sb);
	generateTag(FormErrorsTag.class, sb);

	generateTag(FieldLabelTag.class, sb);
	generateTag(FieldErrorTag.class, sb);

	generateTag(TextBoxTag.class, sb);
	generateTag(DataListTag.class, sb);
	generateTag(DataItemTag.class, sb);
	generateTag(TextAreaTag.class, sb);
	generateTag(CaptchaWordsTag.class, sb);

	generateTag(RadioTag.class, sb);
	generateTag(CheckboxTag.class, sb);
	generateTag(FieldGroupTag.class, sb);

	generateTag(ButtonTag.class, sb);
	generateTag(SubmitButtonTag.class, sb);
	generateTag(ButtonLinkTag.class, sb);

	generateTag(SelectTag.class, sb);
	generateTag(OptionTag.class, sb);

	generateTag(ActionUrlTag.class, sb);
	generateTag(ActionLinkTag.class, sb);
	generateTag(ActionImageTag.class, sb);

	generateTag(IncludeTag.class, sb);

	generateTag(IterateTag.class, sb);
	generateTag(ConstTag.class, sb);

	generateTag(MessagesTag.class, sb);
	generateTag(ControlTag.class, sb);
	generateTag(UserDNameTag.class, sb);

	generateTag(TemplateTag.class, sb);
	generateTag(PagerTag.class, sb);

	generateTag(FmtDaysTag.class, sb);
	generateTag(IfEnvTag.class, sb);

	System.out.println(sb);
    }

    public static void generateTag(Class<?> tagClass, TextBuilder sb) throws Exception {
	Tag tag = tagClass.getDeclaredAnnotation(Tag.class);
	Asserts.notNull(tag, "@Tag is required.");

	String name = tag.name().length() == 0 ? tagClass.getSimpleName().substring(0, tagClass.getSimpleName().lastIndexOf("Tag")) : tag.name();
	StringBuilder desc = new StringBuilder();

	if (!tag.description().isEmpty()) {
	    desc.append(tag.description());
	} else {
	    desc.append(name);
	}
	desc.append("|body=" + tag.bodyContent());
	if (tag.dynamicAttributes()) {
	    desc.append("|dynamicAttributes");
	}
	if (sb.length() == 0) {
	    sb.appendtab().append("<tag>");
	    sb.appendln();
	} else {
	    sb.appendln(2).appendtab().append("<tag>");
	    sb.appendln();
	}
	sb.appendtab(2).append("<description>" + desc + "</description>");
	sb.appendln();
	sb.appendtab(2).append("<name>" + name + "</name>");
	sb.appendln();
	sb.appendtab(2).append("<tag-class>" + tagClass.getName() + "</tag-class>");
	sb.appendln();
	sb.appendtab(2).append("<body-content>" + tag.bodyContent() + "</body-content>");
	sb.appendln();

	List<PropertyDescriptor> attributeProps = new ArrayList<>();
	for (PropertyDescriptor pd : Introspector.getBeanInfo(tagClass).getPropertyDescriptors()) {
	    if (pd.getWriteMethod() != null && (pd.getWriteMethod().getDeclaredAnnotation(Attribute.class) != null)) {
		attributeProps.add(pd);
	    }
	}
	for (PropertyDescriptor pd : attributeProps) {
	    Class<?> type = pd.getWriteMethod().getParameterTypes()[0];

	    Attribute attribute = pd.getWriteMethod().getDeclaredAnnotation(Attribute.class);
	    String attrName = StringUtils.firstLowerCase(pd.getName(), Locale.ENGLISH);
	    StringBuilder attrDesc = new StringBuilder();

	    if (!attribute.description().isEmpty()) {
		attrDesc.append(attribute.description());
	    } else {
		attrDesc.append(attrName);
	    }
	    attrDesc.append("|type=" + type.getName());
	    if (attribute.rtexprvalue()) {
		attrDesc.append("|expression");
	    }
	    if (!attribute.defaultValue().isEmpty()) {
		attrDesc.append("|default=" + attribute.defaultValue());
	    }
	    sb.appendtab(2).append("<attribute>");
	    sb.appendln();
	    sb.appendtab(3).append("<description>" + attrDesc + "</description>");
	    sb.appendln();
	    sb.appendtab(3).append("<name>" + attrName + "</name>");
	    sb.appendln();
	    sb.appendtab(3).append("<required>" + attribute.required() + "</required>");
	    sb.appendln();
	    sb.appendtab(3).append("<rtexprvalue>" + attribute.rtexprvalue() + "</rtexprvalue>");
	    sb.appendln();
	    sb.appendtab(3).append("<type>" + type.getName() + "</type>");
	    sb.appendln();
	    sb.appendtab(2).append("</attribute>");
	    sb.appendln();
	}
	sb.appendtab(2).append("<dynamic-attributes>" + tag.dynamicAttributes() + "</dynamic-attributes>");
	sb.appendln();
	sb.appendtab().append("</tag>");
    }
}
