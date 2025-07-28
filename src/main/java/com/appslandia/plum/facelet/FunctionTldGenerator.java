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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.utils.XmlEscaper;

/**
 *
 * @author Loc Ha
 *
 */
public class FunctionTldGenerator {

  public static void main(String[] args) {
    var sb = new TextBuilder();

    generateFunction(Functions.class, sb);

    System.out.println(sb);
  }

  public static void generateFunction(Class<?> clazz, TextBuilder sb) {
    List<Method> functionMths = new ArrayList<>();
    for (Method method : clazz.getMethods()) {
      if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {

        if (method.getDeclaredAnnotation(Function.class) != null) {
          functionMths.add(method);
        }
      }
    }
    Collections.sort(functionMths, (m1, m2) -> {
      var compare = m1.getDeclaringClass().getName().compareTo(m2.getDeclaringClass().getName());
      if (compare != 0) {
        return compare;
      }
      return m1.getName().compareTo(m2.getName());
    });

    for (Method functionMth : functionMths) {
      var function = functionMth.getDeclaredAnnotation(Function.class);
      var signature = functionSignature(functionMth);

      var name = function.name().length() == 0 ? functionMth.getName() : function.name();
      var desc = new StringBuilder();

      var mth = functionMth.toString();
      if (mth.startsWith("public static ")) {
        mth = mth.substring("public static ".length());
      }
      desc.append("method=").append(mth);

      if (!function.description().isEmpty()) {
        desc.append(";description=").append(function.description());
      } else {
        desc.append(";description=");
      }

      sb.appendtab().append("<function>");
      sb.appendln();

      sb.appendtab(2).append("<description>" + XmlEscaper.escapeContent(desc.toString()) + "</description>");
      sb.appendln();
      sb.appendtab(2).append("<function-name>" + name + "</function-name>");
      sb.appendln();
      sb.appendtab(2).append("<function-class>" + clazz.getName() + "</function-class>");
      sb.appendln();
      sb.appendtab(2).append("<function-signature>" + signature + "</function-signature>");
      sb.appendln();

      sb.appendtab().append("</function>");
      sb.appendln();
    }
  }

  public static String functionSignature(Method method) {
    var sb = new StringBuilder();
    sb.append(method.getReturnType().getName()).append(" ");
    sb.append(method.getName()).append('(');
    var first = true;
    for (Class<?> paramType : method.getParameterTypes()) {
      if (first) {
        sb.append(paramType.getName());
        first = false;
      } else {
        sb.append(", ").append(paramType.getName());
      }
    }
    sb.append(')');
    return sb.toString();
  }
}
