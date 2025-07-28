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
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ActionFilterProvider;
import com.appslandia.plum.base.ActionInvoker;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppContextInitializer;
import com.appslandia.plum.base.AuthContext;
import com.appslandia.plum.base.AuthorizePolicyProvider;
import com.appslandia.plum.base.CaptchaManager;
import com.appslandia.plum.base.ClientIdParser;
import com.appslandia.plum.base.ConfigHeaderHandler;
import com.appslandia.plum.base.ConstDescProvider;
import com.appslandia.plum.base.ControllerProvider;
import com.appslandia.plum.base.CookieHandler;
import com.appslandia.plum.base.CorsPolicyHandler;
import com.appslandia.plum.base.CorsPolicyProvider;
import com.appslandia.plum.base.CsrfManager;
import com.appslandia.plum.base.ElProcessorPool;
import com.appslandia.plum.base.ExceptionHandler;
import com.appslandia.plum.base.ExecutorHandler;
import com.appslandia.plum.base.FormatProviderFactory;
import com.appslandia.plum.base.FormatProviderManager;
import com.appslandia.plum.base.GroupFormatProvider;
import com.appslandia.plum.base.GzipResponseEncoder;
import com.appslandia.plum.base.HeaderPolicyProvider;
import com.appslandia.plum.base.HttpAuthMechanismProvider;
import com.appslandia.plum.base.IdentityHandler;
import com.appslandia.plum.base.InitializerHandler;
import com.appslandia.plum.base.InstanceKey;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.Md5DigestPool;
import com.appslandia.plum.base.ModelBinder;
import com.appslandia.plum.base.PrefCookieHandler;
import com.appslandia.plum.base.RemMeTokenHandler;
import com.appslandia.plum.base.RemMeTokenManager;
import com.appslandia.plum.base.RemoteClientVerifier;
import com.appslandia.plum.base.RequestContextParser;
import com.appslandia.plum.base.ResourcesProvider;
import com.appslandia.plum.base.ResponseEncoder;
import com.appslandia.plum.base.ResponseEncoderProvider;
import com.appslandia.plum.base.ResponseEncodingStrategy;
import com.appslandia.plum.base.ServletContentUtil;
import com.appslandia.plum.base.ServletModuleParser;
import com.appslandia.plum.base.TempDataManager;
import com.appslandia.plum.captcha.CaptchaProducer;
import com.appslandia.plum.defaults.DefaultAppCacheManager;
import com.appslandia.plum.defaults.DefaultClientIdParser;
import com.appslandia.plum.defaults.DefaultFormatProviderFactory;
import com.appslandia.plum.defaults.DefaultHttpAuthenticationMechanismHandler;
import com.appslandia.plum.defaults.DefaultRemMeTokenHandler;
import com.appslandia.plum.defaults.DefaultRemoteClientVerifier;
import com.appslandia.plum.defaults.DefaultServletModuleParser;
import com.appslandia.plum.defaults.HttpAuthenticationMechanismHandler;
import com.appslandia.plum.defaults.MemBasicHttpAuthMechanism;
import com.appslandia.plum.defaults.MemFormHttpAuthMechanism;
import com.appslandia.plum.defaults.MemJwtHttpAuthMechanism;
import com.appslandia.plum.defaults.MemJwtIdentityStore;
import com.appslandia.plum.defaults.MemRemMeTokenManager;
import com.appslandia.plum.defaults.MemUserIdentityHandler;
import com.appslandia.plum.defaults.MemUserIdentityStore;
import com.appslandia.plum.defaults.MemUserService;
import com.appslandia.plum.defaults.MemVersion;

import jakarta.enterprise.inject.Instance;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 *
 * @author Loc Ha
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
    this.objectFactory.getObject(AppContextInitializer.class).contextInitialized(this.servletContext);

    // Register beans obtained via ServletUtils.getAppScoped() because CDI.current() cannot be used
    var beanInstances = AppContextInitializer.getBeanInstances(this.servletContext);

    registerInstance(AppConfig.class, beanInstances);
    registerInstance(LanguageProvider.class, beanInstances);
    registerInstance(ElProcessorPool.class, beanInstances);

    registerInstance(ActionDescProvider.class, beanInstances);
    registerInstance(ActionParser.class, beanInstances);

    registerInstance(ConstDescProvider.class, beanInstances);
    registerInstance(GroupFormatProvider.class, beanInstances);
    registerInstance(TempDataManager.class, beanInstances);
  }

  protected <T> void registerInstance(Class<T> type, Map<InstanceKey, BeanInstance<?>> beanInstances) {
    Instance<T> inst = this.objectFactory.select(type);
    beanInstances.put(new InstanceKey(type, null), new BeanInstance<>(inst.get(), inst));
  }

  protected InitializerHandler getInitializerHandler() {
    var obj = this.initializerHandler;
    if (obj == null) {
      synchronized (this.mutex) {
        if ((obj = this.initializerHandler) == null) {
          obj = new MockInitializerHandler();
          this.objectFactory.inject(obj).postConstruct(obj);
          try {
            obj.init(new MockFilterConfig(this.servletContext, "MockInitializerHandler"));
          } catch (ServletException ex) {
          }
          this.initializerHandler = obj;
        }
      }
    }
    return obj;
  }

  protected ExecutorHandler getExecutorHandler() {
    var obj = this.executorHandler;
    if (obj == null) {
      synchronized (this.mutex) {
        if ((obj = this.executorHandler) == null) {
          obj = new MockExecutorHandler();
          this.objectFactory.inject(obj).postConstruct(obj);
          try {
            obj.init(new MockServletConfig(this.servletContext, "MockExecutorHandler"));
          } catch (ServletException ex) {
          }
          this.executorHandler = obj;
        }
      }
    }
    return obj;
  }

  public MockHttpServletRequest createRequest() {
    initialize();
    var request = new MockHttpServletRequest(this.servletContext);
    return request;
  }

  public MockHttpServletRequest createRequest(String method) {
    var request = createRequest();
    request.setMethod(method);
    return request;
  }

  public MockHttpServletRequest createRequest(String method, String requestURL) {
    var request = createRequest(method);
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
    var httpAuthenticationMechanismHandler = this.objectFactory.getObject(HttpAuthenticationMechanismHandler.class);

    var httpMessageContext = new MockHttpMessageContext().withRequest(request).withResponse(response)
        .withAuthParameters(new AuthenticationParameters());
    httpAuthenticationMechanismHandler.validateRequest(request, response, httpMessageContext);
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
    var factory = new ObjectFactory();

    factory.register(Md5DigestPool.class, Md5DigestPool.class);
    factory.register(ServletContentUtil.class, ServletContentUtil.class);
    factory.register(ElProcessorPool.class, ElProcessorPool.class);
    factory.register(AppContextInitializer.class, AppContextInitializer.class);

    factory.register(AppConfig.class, MockAppConfig.class);
    factory.register(AppLogger.class, MockAppLogger.class);
    factory.register(ExceptionHandler.class, MockExceptionHandler.class);

    factory.register(JsonProcessor.class, GsonProcessor.class);
    factory.register(JsonProcessor.class, GsonProcessor.class, null, new Annotation[] { JsonLiteral.COMPACT });
    factory.register(JsonProcessor.class, GsonProcessor.class, null, new Annotation[] { JsonLiteral.PRETTY });

    factory.register(ActionParser.class, ActionParser.class);
    factory.register(ActionDescProvider.class, MockActionDescProvider.class);
    factory.register(ActionFilterProvider.class, MockActionFilterProvider.class);

    factory.register(ModelBinder.class, ModelBinder.class);
    factory.register(ActionInvoker.class, ActionInvoker.class);
    factory.register(ControllerProvider.class, MockControllerProvider.class);

    factory.register(HttpAuthenticationMechanism.class, MemFormHttpAuthMechanism.class);
    factory.register(HttpAuthenticationMechanism.class, MemBasicHttpAuthMechanism.class);
    factory.register(HttpAuthenticationMechanism.class, MemJwtHttpAuthMechanism.class);
    factory.register(HttpAuthMechanismProvider.class, MockHttpAuthMechanismProvider.class);
    factory.register(HttpAuthenticationMechanismHandler.class, DefaultHttpAuthenticationMechanismHandler.class);

    factory.register(AuthContext.class, AuthContext.class);
    factory.register(SecurityContext.class, MockSecurityContext.class);
    factory.register(IdentityHandler.class, MemUserIdentityHandler.class);
    factory.register(IdentityStoreHandler.class, MockIdentityStoreHandler.class);
    factory.register(AccessRateHandler.class, MockAccessRateHandler.class);

    factory.register(MemUserService.class, MemUserService.class);
    factory.register(IdentityStore.class, MemUserIdentityStore.class);
    factory.register(IdentityStore.class, MemJwtIdentityStore.class);

    factory.register(ResponseEncoder.class, GzipResponseEncoder.class);
    factory.register(ResponseEncodingStrategy.class, ResponseEncodingStrategy.class);
    factory.register(ResponseEncoderProvider.class, MockResponseEncoderProvider.class);

    factory.register(RemMeTokenManager.class, MemRemMeTokenManager.class);
    factory.register(RemMeTokenHandler.class, DefaultRemMeTokenHandler.class);

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
    factory.register(ConstDescProvider.class, ConstDescProvider.class);
    factory.register(GroupFormatProvider.class, GroupFormatProvider.class);

    factory.register(CorsPolicyProvider.class, CorsPolicyProvider.class);
    factory.register(CorsPolicyHandler.class, CorsPolicyHandler.class);
    factory.register(HeaderPolicyProvider.class, HeaderPolicyProvider.class);
    factory.register(ConfigHeaderHandler.class, ConfigHeaderHandler.class);

    factory.register(CaptchaManager.class, MockCaptchaManager.class);
    factory.register(CaptchaProducer.class, MockCaptchaProducer.class);
    factory.register(CsrfManager.class, MockCsrfManager.class);
    factory.register(TempDataManager.class, MockTempDataManager.class);

    factory.register(CookieHandler.class, CookieHandler.class);
    factory.register(PrefCookieHandler.class, PrefCookieHandler.class);
    factory.register(AppCacheManager.class, DefaultAppCacheManager.class);

    factory.register(JwtSigner.class, new ObjectProducer<JwtSigner>() {

      @MemVersion
      @Override
      public JwtSigner produce(ObjectFactory factory) throws ObjectException {
        var gsonProcessor = new GsonProcessor().setBuilder(JoseGson.newGsonBuilder(true, false));

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
