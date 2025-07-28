// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

package com.appslandia.plum.facelet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.stream.JoiningAsStringCollector;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.XmlEscaper;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.view.facelets.TagHandler;

/**
 *
 * @author Loc Ha
 *
 */
public class TagTldGenerator {

  public static void main(String[] args) throws Exception {
    var sb = new TextBuilder();

    List<String> warnings = new ArrayList<>();
    List<Class<?>> dynClasses = new ArrayList<>();
    List<Class<?>> notDynClasses = new ArrayList<>();

    generateTag(FormTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(FormErrorsTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(FormActionTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(FieldLabelTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(FieldErrorTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(HiddenTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(InputTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(DataListTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(TextAreaTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(ChoiceInputTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(ChoiceLabelTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(ChoiceBoxTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(ButtonTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(SelectTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(CsrfIdTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(CaptchaIdTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(CaptchaTextTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(CaptchaImgTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(CaptchaRefreshUrlTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(ActionUrlTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(ActionLinkTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(ActionImageTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(LinkTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(UrlTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(ConstTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(SymbolTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(CheckOrXTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(CheckMarkTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(MessagesTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(AuthTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(UnauthTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(UserNameTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(FmtGroupTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(TestViewErrorTag.class, sb, dynClasses, notDynClasses, warnings);

    generateTag(WrapperUITag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(IfTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(IterateTag.class, sb, dynClasses, notDynClasses, warnings);
    generateTag(ScriptJsonTag.class, sb, dynClasses, notDynClasses, warnings);

    System.out.println("***** Warnings *****");
    System.out.println(warnings.stream().collect(new JoiningAsStringCollector<String>()));
    System.out.println();

    System.out.println("***** DynamicAttributes *****");
    System.out.println(dynClasses.stream().map(c -> c.getSimpleName()).collect(new JoiningAsStringCollector<String>()));
    System.out.println();

    System.out.println("***** NOT DynamicAttributes *****");
    System.out
        .println(notDynClasses.stream().map(c -> c.getSimpleName()).collect(new JoiningAsStringCollector<String>()));
    System.out.println();

    System.out.println("***** Tablib XML *****");
    System.out.println(sb);
  }

  public static void generateTag(Class<?> tagClass, TextBuilder sb, List<Class<?>> dynClasses,
      List<Class<?>> notDynClasses, List<String> warnings) throws Exception {

    var tag = tagClass.getDeclaredAnnotation(Tag.class);
    Asserts.notNull(tag, "@com.appslandia.plum.facelet.Tag is required.");

    if (tag.dynamicAttributes()) {
      dynClasses.add(tagClass);
    } else {
      notDynClasses.add(tagClass);
    }

    var name = tag.name();
    Asserts.isTrue(name.length() > 0, "name is required.");

    Asserts.isTrue(UIComponentBase.class.isAssignableFrom(tagClass) || TagHandler.class.isAssignableFrom(tagClass),
        "generateTag() only accepts either a UIComponentBase or a TagHandler class.");

    var desc = new StringBuilder();
    var isComponent = UIComponentBase.class.isAssignableFrom(tagClass);

    if (isComponent) {
      desc.append("component");
    } else {
      desc.append("tag-handler");
    }

    if (isComponent) {

      if (tag.dynamicAttributes()) {
        Asserts.isTrue(DynamicAttributes.class.isAssignableFrom(tagClass),
            "dynamicAttributes=true, but the '{}' is not DynamicAttributes.", tagClass);
      } else {
        if (DynamicAttributes.class.isAssignableFrom(tagClass)) {
          warnings.add(STR.fmt("WARNING: dynamicAttributes=false, but the '{}' is DynamicAttributes.", tagClass));
        }
      }

      if (tag.dynamicAttributes()) {
        desc.append(";dynamicAttributes");
      } else {
        desc.append(";dynamicAttributes=false");
      }
    } else {
      // TagHandler
      desc.append(";dynamicAttributes");
    }

    if (tag.bodyContent()) {
      desc.append(";bodyContent");
    } else {
      desc.append(";bodyContent=false");
    }
    desc.append(";type=").append(tagClass.getName());

    if (!tag.description().isEmpty()) {
      desc.append(";description=").append(tag.description());
    } else {
      desc.append(";description=").append(tag.name());
    }

    sb.appendtab().append("<tag>");
    sb.appendln();
    sb.appendtab(2).append("<description>" + XmlEscaper.escapeContent(desc.toString()) + "</description>");
    sb.appendln();
    sb.appendtab(2).append("<tag-name>" + name + "</tag-name>");
    sb.appendln();

    if (isComponent) {
      var facesComponent = tagClass.getDeclaredAnnotation(FacesComponent.class);
      Asserts.notNull(facesComponent, "@FacesComponent annotation is required on class: {}", tagClass);
      Asserts.isTrue(facesComponent.value().equals(FlComponent.COMPONENT_FAMILY + "." + tagClass.getSimpleName()),
          "Invalid component type: " + facesComponent.value());

      sb.appendtab(2).append("<component>");
      sb.appendln();
      sb.appendtab(3).append("<component-type>" + facesComponent.value() + "</component-type>");
      sb.appendln();

      if (tag.dynamicAttributes()) {
        sb.appendtab(3).append("<handler-class>com.appslandia.plum.facelet.DynamicAttributesHandler</handler-class>");
        sb.appendln();
      }
      sb.appendtab(2).append("</component>");
      sb.appendln();
    } else {
      sb.appendtab(2).append("<handler-class>" + tagClass.getName() + "</handler-class>");
      sb.appendln();
    }

    // attributes
    var attributes = tag.attributes();
    Arrays.sort(attributes, (a1, a2) -> {
      if (a1.required() && !a2.required()) {
        return -1;
      }
      if (!a1.required() && a2.required()) {
        return 1;
      }
      return a1.name().compareTo(a2.name());
    });

    for (Attribute attribute : attributes) {
      Class<?> type = attribute.type();
      var attrName = attribute.name();

      var attrDesc = new StringBuilder();

      if (attribute.required()) {
        attrDesc.append("required");
      } else {
        attrDesc.append("required=false");
      }
      attrDesc.append(";type=" + type.getName());

      if (!attribute.domain().isEmpty()) {
        attrDesc.append(";domain=" + attribute.domain());
      }
      if (!attribute.description().isEmpty()) {
        attrDesc.append(";description=").append(attribute.description());
      } else {
        attrDesc.append(";description=").append(attrName);
      }

      sb.appendtab(2).append("<attribute>");
      sb.appendln();
      sb.appendtab(3).append("<description>" + XmlEscaper.escapeContent(attrDesc.toString()) + "</description>");
      sb.appendln();
      sb.appendtab(3).append("<name>" + attrName + "</name>");
      sb.appendln();
      sb.appendtab(3).append("<required>" + attribute.required() + "</required>");
      sb.appendln();
      sb.appendtab(3).append("<type>" + type.getName() + "</type>");
      sb.appendln();
      sb.appendtab(2).append("</attribute>");
      sb.appendln();
    }
    sb.appendtab().append("</tag>");
    sb.appendln();
  }
}
