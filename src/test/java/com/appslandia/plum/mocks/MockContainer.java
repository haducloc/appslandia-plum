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

package com.appslandia.plum.mocks;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.appslandia.common.base.AppLogger;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.cdi.JsonLiteral;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.factory.ObjectException;
import com.appslandia.common.factory.ObjectFactory;
import com.appslandia.common.factory.ObjectProducer;
import com.appslandia.common.jose.HsJwtSigner;
import com.appslandia.common.jose.JoseGson;
import com.appslandia.common.jose.JwtSigner;
import com.appslandia.common.json.GsonProcessor;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.threading.ThreadLocalStorage;
import com.appslandia.plum.base.AccessRateHandler;
import com.appslandia.plum.base.AccessRateSkipper;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ActionFilterProvider;
import com.appslandia.plum.base.ActionInvoker;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AuthContext;
import com.appslandia.plum.base.AuthHandler;
import com.appslandia.plum.base.AuthHandlerProvider;
import com.appslandia.plum.base.AuthTokenHandler;
import com.appslandia.plum.base.AuthTokenManager;
import com.appslandia.plum.base.AuthorizePolicyProvider;
import com.appslandia.plum.base.BeanInstanceContextListener;
import com.appslandia.plum.base.CaptchaManager;
import com.appslandia.plum.base.ClientIdParser;
import com.appslandia.plum.base.ConstDescProvider;
import com.appslandia.plum.base.ControllerProvider;
import com.appslandia.plum.base.CookieHandler;
import com.appslandia.plum.base.CorsPolicyHandler;
import com.appslandia.plum.base.CorsPolicyProvider;
import com.appslandia.plum.base.CsrfManager;
import com.appslandia.plum.base.ExceptionHandler;
import com.appslandia.plum.base.ExecutorHandler;
import com.appslandia.plum.base.FormatProviderFactory;
import com.appslandia.plum.base.FormatProviderManager;
import com.appslandia.plum.base.GroupFormatProvider;
import com.appslandia.plum.base.GzipResponseEncoder;
import com.appslandia.plum.base.HeaderPolicyProvider;
import com.appslandia.plum.base.HttpAuthenticationMechanismBase;
import com.appslandia.plum.base.IdentityValidator;
import com.appslandia.plum.base.InitializerHandler;
import com.appslandia.plum.base.InstanceKey;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.ModelBinder;
import com.appslandia.plum.base.PrefCookieHandler;
import com.appslandia.plum.base.RemoteClientVerifier;
import com.appslandia.plum.base.RequestContextParser;
import com.appslandia.plum.base.ResourcesProvider;
import com.appslandia.plum.base.ResponseEncoder;
import com.appslandia.plum.base.ResponseEncoderProvider;
import com.appslandia.plum.base.ServletModuleParser;
import com.appslandia.plum.base.TagCookieHandler;
import com.appslandia.plum.base.TempDataManager;
import com.appslandia.plum.captcha.CaptchaProducer;
import com.appslandia.plum.defaults.DefaultAccessRateSkipper;
import com.appslandia.plum.defaults.DefaultAuthTokenHandler;
import com.appslandia.plum.defaults.DefaultClientIdParser;
import com.appslandia.plum.defaults.DefaultFormatProviderFactory;
import com.appslandia.plum.defaults.DefaultHttpAuthenticationMechanism;
import com.appslandia.plum.defaults.DefaultIdentityValidator;
import com.appslandia.plum.defaults.DefaultRemoteClientVerifier;
import com.appslandia.plum.defaults.DefaultServletModuleParser;
import com.appslandia.plum.defaults.MemAppCacheManager;
import com.appslandia.plum.defaults.MemAuthTokenManager;
import com.appslandia.plum.pebble.PebbleExtensionProvider;
import com.appslandia.plum.pebble.PebbleTemplateProvider;
import com.appslandia.plum.pebble.functions.DefaultPebbleExtensionProvider;

import jakarta.enterprise.inject.Instance;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MockContainer extends InitializeObject {

  public static final ThreadLocalStorage<MockContainer> containerHolder = new ThreadLocalStorage<>();
  public static final ThreadLocalStorage<MockHttpServletRequest> currentRequestHolder = new ThreadLocalStorage<>(true);
  public static final ThreadLocalStorage<MockHttpServletResponse> currentResponseHolder = new ThreadLocalStorage<>(
      true);

  final MockServletContext servletContext;
  final ObjectFactory objectFactory;

  private volatile InitializerHandler initializerHandler;
  private volatile ExecutorHandler executorHandler;

  final Object mutex = new Object();

  public MockContainer() {
    this.servletContext = new MockServletContext(new MockSessionCookieConfig());
    this.objectFactory = createObjectFactory(this.servletContext);
  }

  @Override
  protected void init() throws Exception {
    this.objectFactory.getObject(BeanInstanceContextListener.class).contextInitialized(this.servletContext);

    Map<InstanceKey, BeanInstance<?>> beanInstances = BeanInstanceContextListener.getBeanInstances(this.servletContext);

    registerInstance(AppConfig.class, beanInstances);
    registerInstance(LanguageProvider.class, beanInstances);

    registerInstance(ActionDescProvider.class, beanInstances);
    registerInstance(ActionParser.class, beanInstances);
    registerInstance(ActionInvoker.class, beanInstances);
    registerInstance(ControllerProvider.class, beanInstances);

    registerInstance(ConverterProvider.class, beanInstances);
    registerInstance(TempDataManager.class, beanInstances);
    registerInstance(AppCacheManager.class, beanInstances);

    registerInstance(ConstDescProvider.class, beanInstances);
    registerInstance(GroupFormatProvider.class, beanInstances);

    registerInstance(ModelBinder.class, beanInstances);
    registerInstance(ExceptionHandler.class, beanInstances);

    registerInstance(PebbleTemplateProvider.class, beanInstances);
  }

  protected <T> void registerInstance(Class<T> type, Map<InstanceKey, BeanInstance<?>> beanInstances) {
    Instance<T> inst = this.objectFactory.select(type);
    beanInstances.put(new InstanceKey(type, null), new BeanInstance<>(inst.get(), inst));
  }

  protected InitializerHandler getInitializerHandler() {
    InitializerHandler obj = this.initializerHandler;
    if (obj == null) {
      synchronized (this.mutex) {
        if ((obj = this.initializerHandler) == null) {
          obj = new MockInitializerHandler();
          this.initializerHandler = this.objectFactory.inject(obj).postConstruct(obj);
        }
      }
    }
    return obj;
  }

  protected ExecutorHandler getExecutorHandler() {
    ExecutorHandler obj = this.executorHandler;
    if (obj == null) {
      synchronized (this.mutex) {
        if ((obj = this.executorHandler) == null) {
          obj = new MockExecutorHandler().setServletConfig(new MockServletConfig(this.servletContext));
          this.executorHandler = this.objectFactory.inject(obj).postConstruct(obj);
        }
      }
    }
    return obj;
  }

  public MockHttpServletRequest createRequest() {
    initialize();
    MockHttpServletRequest request = new MockHttpServletRequest(this.servletContext);
    return request;
  }

  public MockHttpServletRequest createRequest(String method) {
    MockHttpServletRequest request = createRequest();
    request.setMethod(method);
    return request;
  }

  public MockHttpServletRequest createRequest(String method, String requestURL) {
    MockHttpServletRequest request = createRequest(method);
    request.setRequestURL(requestURL);
    return request;
  }

  public MockHttpServletResponse createResponse() {
    initialize();
    return new MockHttpServletResponse(this.servletContext);
  }

  public <T, I extends T> I getObject(Class<T> type) {
    return this.objectFactory.getObject(type);
  }

  public <T, I extends T> I getObject(Class<T> type, Annotation... qualifiers) {
    return this.objectFactory.getObject(type, qualifiers);
  }

  public MockAppConfig getAppConfig() {
    return this.objectFactory.getObject(AppConfig.class);
  }

  public <T> MockContainer register(Class<T> type, Class<? extends T> impl) {
    assertNotInitialized();
    this.objectFactory.register(type, impl);
    return this;
  }

  public MockContainer unregister(Class<?> type) {
    assertNotInitialized();
    this.objectFactory.unregister(type);
    return this;
  }

  protected void executeAuthenticationMechanism(MockHttpServletRequest request, MockHttpServletResponse response)
      throws AuthenticationException {
    HttpAuthenticationMechanism authenticationMechanism = this.objectFactory
        .getObject(HttpAuthenticationMechanism.class);

    MockHttpMessageContext httpMessageContext = new MockHttpMessageContext().withRequest(request).withResponse(response)
        .withAuthParameters(new AuthenticationParameters());
    authenticationMechanism.validateRequest(request, response, httpMessageContext);
  }

  public void execute(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
    initialize();
    executeAuthenticationMechanism(request, response);
    new MockFilterChain().addFilter(getInitializerHandler()).setServlet(getExecutorHandler()).doFilter(request,
        response);

    if (response.getStatus() < 300 || response.getStatus() >= 400) {
      response.flushBuffer();
    }
  }

  protected ObjectFactory createObjectFactory(final ServletContext sc) {
    ObjectFactory factory = new ObjectFactory();
    factory.register(BeanInstanceContextListener.class, BeanInstanceContextListener.class);

    factory.register(AppConfig.class, MockAppConfig.class);
    factory.register(AppLogger.class, MockAppLogger.class);
    factory.register(ExceptionHandler.class, MockExceptionHandler.class);

    factory.register(AccessRateSkipper.class, DefaultAccessRateSkipper.class);
    factory.register(AccessRateHandler.class, MockAccessRateHandler.class);

    factory.register(JsonProcessor.class, GsonProcessor.class);
    factory.register(JsonProcessor.class, GsonProcessor.class, null, new Annotation[] { JsonLiteral.COMPACT });

    factory.register(AppCacheManager.class, MemAppCacheManager.class);

    factory.register(ActionParser.class, ActionParser.class);
    factory.register(ActionDescProvider.class, MockActionDescProvider.class);
    factory.register(ActionFilterProvider.class, MockActionFilterProvider.class);

    factory.register(ModelBinder.class, ModelBinder.class);
    factory.register(ActionInvoker.class, ActionInvoker.class);
    factory.register(ControllerProvider.class, MockControllerProvider.class);

    factory.register(new Class<?>[] { HttpAuthenticationMechanism.class, HttpAuthenticationMechanismBase.class },
        DefaultHttpAuthenticationMechanism.class);
    factory.register(SecurityContext.class, MockSecurityContext.class);
    factory.register(IdentityValidator.class, DefaultIdentityValidator.class);
    factory.register(AuthContext.class, AuthContext.class);
    factory.register(IdentityStoreHandler.class, MockIdentityStoreHandler.class);

    factory.register(IdentityStore.class, MemUserPasswordIdentityStore.class);
    factory.register(IdentityStore.class, MemJwtIdentityStore.class);

    factory.register(AuthHandler.class, MemBasicAuthHandler.class);
    factory.register(AuthHandler.class, MemBearerAuthHandler.class);
    factory.register(AuthHandler.class, MemFormAuthHandler.class);
    factory.register(AuthHandlerProvider.class, MockAuthHandlerProvider.class);

    factory.register(ResponseEncoder.class, GzipResponseEncoder.class);
    factory.register(ResponseEncoderProvider.class, MockResponseEncoderProvider.class);

    factory.register(MemUserDatabase.class, MemUserDatabase.class);
    factory.register(AuthTokenManager.class, MemAuthTokenManager.class);
    factory.register(AuthTokenHandler.class, DefaultAuthTokenHandler.class);

    factory.register(ClientIdParser.class, DefaultClientIdParser.class);
    factory.register(ServletModuleParser.class, DefaultServletModuleParser.class);
    factory.register(RemoteClientVerifier.class, DefaultRemoteClientVerifier.class);
    factory.register(AuthorizePolicyProvider.class, AuthorizePolicyProvider.class);

    factory.register(ConverterProvider.class, ConverterProvider.class);
    factory.register(LanguageProvider.class, MockLanguageProvider.class);
    factory.register(FormatProviderManager.class, FormatProviderManager.class);
    factory.register(FormatProviderFactory.class, DefaultFormatProviderFactory.class);

    factory.register(ResourcesProvider.class, MockResourcesProvider.class);
    factory.register(RequestContextParser.class, RequestContextParser.class);

    factory.register(CorsPolicyProvider.class, CorsPolicyProvider.class);
    factory.register(CorsPolicyHandler.class, CorsPolicyHandler.class);
    factory.register(HeaderPolicyProvider.class, HeaderPolicyProvider.class);
    factory.register(ConstDescProvider.class, ConstDescProvider.class);
    factory.register(GroupFormatProvider.class, GroupFormatProvider.class);

    factory.register(CaptchaManager.class, MockCaptchaManager.class);
    factory.register(CaptchaProducer.class, MockCaptchaProducer.class);
    factory.register(CsrfManager.class, MockCsrfManager.class);
    factory.register(TempDataManager.class, MockTempDataManager.class);

    factory.register(CookieHandler.class, CookieHandler.class);
    factory.register(PrefCookieHandler.class, PrefCookieHandler.class);
    factory.register(TagCookieHandler.class, TagCookieHandler.class);

    // PebbleTemplateProvider
    factory.register(PebbleTemplateProvider.class, MemPebbleTemplateProvider.class);
    factory.register(PebbleExtensionProvider.class, DefaultPebbleExtensionProvider.class);

    factory.register(JwtSigner.class, new ObjectProducer<JwtSigner>() {

      @MemVersion
      @Override
      public JwtSigner produce(ObjectFactory factory) throws ObjectException {
        GsonProcessor gsonProcessor = new GsonProcessor().setBuilder(JoseGson.newGsonBuilder(true, false));

        return HsJwtSigner.HS256().setJsonProcessor(gsonProcessor).setSecret("secret".getBytes()).build();
      }
    });

    factory.register(Validator.class, new ObjectProducer<Validator>() {

      @Override
      public Validator produce(ObjectFactory factory) throws ObjectException {
        return Validation.buildDefaultValidatorFactory().getValidator();
      }
    });

    factory.register(ServletContext.class, new ObjectProducer<ServletContext>() {
      @Override
      public ServletContext produce(ObjectFactory factory) throws ObjectException {
        return sc;
      }
    });

    factory.register(HttpServletRequest.class, new ObjectProducer<HttpServletRequest>() {
      @Override
      public HttpServletRequest produce(ObjectFactory factory) throws ObjectException {
        return new MockCurrentRequest();
      }
    });
    return factory;
  }
}
