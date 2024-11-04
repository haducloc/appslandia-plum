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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.appslandia.common.base.Out;

import jakarta.enterprise.util.AnnotationLiteral;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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
    if (method.getDeclaringClass() == Object.class) {
      return false;
    }
    if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
      return false;
    }
    if (method.getDeclaredAnnotation(Removed.class) != null) {
      return false;
    }
    if (method.getDeclaredAnnotation(ChildAction.class) != null) {
      return false;
    }
    Out<Boolean> httpMethod = new Out<>();
    ActionDescProvider.parseAllowMethods(method, httpMethod);

    if (!Boolean.TRUE.equals(httpMethod.value)) {
      return false;
    }
    return true;
  }

  public static CacheControl createCacheControl(String policy) {
    return new CacheControlLiteral(policy);
  }

  public static EnableJsonError createEnableJsonError() {
    return new EnableJsonErrorLiteral();
  }

  public static ConsumeType createConsumeType(String contentType) {
    return new ConsumeTypeLiteral(contentType);
  }

  @SuppressWarnings("all")
  public static class CacheControlLiteral extends AnnotationLiteral<CacheControl> implements CacheControl {
    private static final long serialVersionUID = 1L;

    final String policy;

    public CacheControlLiteral(String policy) {
      this.policy = policy;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return CacheControl.class;
    }

    @Override
    public String value() {
      return this.policy;
    }

    @Override
    public boolean nocache() {
      return CacheControl.NO_CACHE_POLICY.equals(policy);
    }
  }

  @SuppressWarnings("all")
  public static class EnableJsonErrorLiteral extends AnnotationLiteral<EnableJsonError> implements EnableJsonError {
    private static final long serialVersionUID = 1L;

    public EnableJsonErrorLiteral() {
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return EnableJsonError.class;
    }
  }

  @SuppressWarnings("all")
  public static class ConsumeTypeLiteral extends AnnotationLiteral<ConsumeType> implements ConsumeType {
    private static final long serialVersionUID = 1L;

    final String contentType;

    public ConsumeTypeLiteral(String contentType) {
      this.contentType = contentType;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return ConsumeType.class;
    }

    @Override
    public String value() {
      return this.contentType;
    }
  }
}
