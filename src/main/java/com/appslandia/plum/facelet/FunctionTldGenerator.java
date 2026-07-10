// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
