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

package com.appslandia.plum.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.appslandia.common.base.Out;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionDescUtils {

  public static boolean isActionResultOrVoid(Method actionMethod) {
    return ActionResult.class.isAssignableFrom(actionMethod.getReturnType())
        || (actionMethod.getReturnType() == void.class);
  }

  public static boolean isJsonError(ActionDesc actionDesc) {
    return (actionDesc != null) ? (actionDesc.getEnableJsonError() != null) : false;
  }

  public static boolean isActionMethod(Method method) {
    if ((method.getDeclaringClass() == Object.class) || !Modifier.isPublic(method.getModifiers())
        || Modifier.isStatic(method.getModifiers()) || (method.getDeclaredAnnotation(Removed.class) != null)) {
      return false;
    }

    var httpMethod = new Out<Boolean>();
    ActionDescProvider.parseAllowMethods(method, httpMethod);

    if (!Boolean.TRUE.equals(httpMethod.value)) {
      return false;
    }
    return true;
  }
}
