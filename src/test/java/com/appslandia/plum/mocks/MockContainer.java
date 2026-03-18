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

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.caching.AppCacheManager;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.cdi.Json;
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
import com.appslandia.plum.base.AccessLog;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.ActionFilterProvider;
import com.appslandia.plum.base.ActionInvoker;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppContextInitializer;
import com.appslandia.plum.base.AppFilterHandler;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.AppPolicyProvider;
import com.appslandia.plum.base.AuthContext;
import com.appslandia.plum.base.CaptchaManager;
import com.appslandia.plum.base.ClientUrlInfoParser;
import com.appslandia.plum.base.CompressHandler;
import com.appslandia.plum.base.CompressHandlerProvider;
import com.appslandia.plum.base.ConstDescProvider;
import com.appslandia.plum.base.ControllerProvider;
import com.appslandia.plum.base.CookieHandler;
import com.appslandia.plum.base.CorsPolicyHandler;
import com.appslandia.plum.base.CsrfManager;
import com.appslandia.plum.base.ElProcessorPool;
import com.appslandia.plum.base.ExceptionHandler;
import com.appslandia.plum.base.ExecutorHandler;
import com.appslandia.plum.base.FormatProviderFactory;
import com.appslandia.plum.base.GroupFormatProvider;
import com.appslandia.plum.base.GzipCompressHandler;
import com.appslandia.plum.base.HttpAuthMechanismProvider;
import com.appslandia.plum.base.IdentityValidator;
import com.appslandia.plum.base.IdentityValidatorProvider;
import com.appslandia.plum.base.InitialRequestGuard;
import com.appslandia.plum.base.InitializerHandler;
import com.appslandia.plum.base.InstanceKey;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.Md5DigestPool;
import com.appslandia.plum.base.ModelBinder;
import com.appslandia.plum.base.PrefCookieHandler;
import com.appslandia.plum.base.RemMeTokenHandler;
import com.appslandia.plum.base.RemMeTokenManager;
import com.appslandia.plum.base.RequestContextParser;
import com.appslandia.plum.base.ResourcesProvider;
import com.appslandia.plum.base.ServletContentUtil;
import com.appslandia.plum.base.TempDataManager;
import com.appslandia.plum.captcha.CaptchaProducer;
import com.appslandia.plum.defaults.DefaultAppCacheManager;
import com.appslandia.plum.defaults.DefaultClientUrlInfoParser;
import com.appslandia.plum.defaults.DefaultFormatProviderFactory;
import com.appslandia.plum.defaults.DefaultHttpAuthenticationMechanismHandler;
import com.appslandia.plum.defaults.DefaultInitialRequestGuard;
import com.appslandia.plum.defaults.DefaultRemMeTokenHandler;
import com.appslandia.plum.defaults.DefaultRemMeTokenManager;
import com.appslandia.plum.mem.MemBasicHttpAuthMechanism;
import com.appslandia.plum.mem.MemBasicIdentityValidator;
import com.appslandia.plum.mem.MemFormHttpAuthMechanism;
import com.appslandia.plum.mem.MemFormIdentityValidator;
import com.appslandia.plum.mem.MemJwtHttpAuthMechanism;
import com.appslandia.plum.mem.MemJwtIdentityStore;
import com.appslandia.plum.mem.MemJwtIdentityValidator;
import com.appslandia.plum.mem.MemUserIdentityStore;
import com.appslandia.plum.mem.MemUserService;
import com.appslandia.plum.mem.MemVersion;

import jakarta.enterprise.inject.Instance;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanismHandler;
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
public class MockContainer extends InitializingObject {

  public static final ThreadLocalStorage<MockContainer> containerHolder = new ThreadLocalStorage<>();
  public static final ThreadLocalStorage<MockHttpServletRequest> currentRequestHolder = new ThreadLocalStorage<>(true);
  public static final ThreadLocalStorage<MockHttpServletResponse> currentResponseHolder = new ThreadLocalStorage<>(
      true);

  final MockServletContext servletContext;
  final ObjectFactory objectFactory;

  private volatile AppFilterHandler appFilterHandler;
  private volatile InitializerHandler initializerHandler;
  private volatile ExecutorHandler executorHandler;

  final Object mutex = new Object();

  public MockContainer() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
    objectFactory = createObjectFactory(servletContext);
  }

  @Override
  protected void init() throws Exception {
    objectFactory.getObject(AppContextInitializer.class).contextInitialized(servletContext);

    // Register beans obtained via ServletUtils.getAppScoped() because CDI.current() cannot be used
    var beanInstances = AppContextInitializer.getBeanInstances(servletContext);

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
    Instance<T> inst = objectFactory.select(type);
    beanInstances.put(new InstanceKey(type, null), new BeanInstance<>(inst.get(), inst));
  }

  protected AppFilterHandler getAppFilterHandler() {
    var obj = appFilterHandler;
    if (obj == null) {
      synchronized (mutex) {
        if ((obj = appFilterHandler) == null) {
          obj = new AppFilterHandler();
          objectFactory.inject(obj).postConstruct(obj);
          try {
            obj.init(new MockFilterConfig(servletContext, "AppFilterHandler"));
          } catch (ServletException ex) {
          }
          appFilterHandler = obj;
        }
      }
    }
    return obj;
  }

  protected InitializerHandler getInitializerHandler() {
    var obj = initializerHandler;
    if (obj == null) {
      synchronized (mutex) {
        if ((obj = initializerHandler) == null) {
          obj = new MockInitializerHandler();
          objectFactory.inject(obj).postConstruct(obj);
          try {
            obj.init(new MockFilterConfig(servletContext, "MockInitializerHandler"));
          } catch (ServletException ex) {
          }
          initializerHandler = obj;
        }
      }
    }
    return obj;
  }

  protected ExecutorHandler getExecutorHandler() {
    var obj = executorHandler;
    if (obj == null) {
      synchronized (mutex) {
        if ((obj = executorHandler) == null) {
          obj = new MockExecutorHandler();
          objectFactory.inject(obj).postConstruct(obj);
          try {
            obj.init(new MockServletConfig(servletContext, "MockExecutorHandler"));
          } catch (ServletException ex) {
          }
          executorHandler = obj;
        }
      }
    }
    return obj;
  }

  public MockHttpServletRequest createRequest() {
    initialize();
    var request = new MockHttpServletRequest(servletContext);
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
    return new MockHttpServletResponse(servletContext);
  }

  public <T, I extends T> I getObject(Class<T> type) {
    return objectFactory.getObject(type);
  }

  public <T, I extends T> I getObject(Class<T> type, Annotation... qualifiers) {
    return objectFactory.getObject(type, qualifiers);
  }

  public MockAppConfig getAppConfig() {
    return objectFactory.getObject(AppConfig.class);
  }

  public <T> MockContainer register(Class<T> type, Class<? extends T> impl) {
    assertNotInitialized();
    objectFactory.register(type, impl);
    return this;
  }

  public MockContainer unregister(Class<?> type) {
    assertNotInitialized();
    objectFactory.unregister(type);
    return this;
  }

  protected void executeAuthenticationMechanism(MockHttpServletRequest request, MockHttpServletResponse response)
      throws AuthenticationException {
    var httpAuthenticationMechanismHandler = objectFactory.getObject(HttpAuthenticationMechanismHandler.class);
    var httpMessageContext = new MockHttpMessageContext().withRequest(request).withResponse(response)
        .withAuthParameters(new AuthenticationParameters());

    httpAuthenticationMechanismHandler.validateRequest(request, response, httpMessageContext);
  }

  public void execute(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
    initialize();
    try {
      executeAuthenticationMechanism(request, response);

      new MockFilterChain().addFilter(getAppFilterHandler()).addFilter(getInitializerHandler())
          .setServlet(getExecutorHandler()).doFilter(request, response);

      if (response.getStatus() < 300 || response.getStatus() >= 400) {
        response.flushBuffer();
      }

    } catch (Exception uncatchedEx) {
      request.setAttribute(Throwable.class.getName(), uncatchedEx);
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
    factory.register(AppLogger.class, MockAppLogger.class, null, new Annotation[] { AccessLog.IMPL });
    factory.register(ExceptionHandler.class, MockExceptionHandler.class);

    factory.register(JsonProcessor.class, GsonProcessor.class);
    factory.register(JsonProcessor.class, GsonProcessor.class, null, new Annotation[] { Json.COMPACT });
    factory.register(JsonProcessor.class, GsonProcessor.class, null, new Annotation[] { Json.PRETTY });

    factory.register(ActionParser.class, ActionParser.class);
    factory.register(ActionDescProvider.class, MockActionDescProvider.class);

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
    factory.register(IdentityStoreHandler.class, MockIdentityStoreHandler.class);

    factory.register(IdentityValidator.class, MemFormIdentityValidator.class);
    factory.register(IdentityValidator.class, MemBasicIdentityValidator.class);
    factory.register(IdentityValidator.class, MemJwtIdentityValidator.class);
    factory.register(IdentityValidatorProvider.class, MockIdentityValidatorProvider.class);

    factory.register(MemUserService.class, MemUserService.class);
    factory.register(IdentityStore.class, MemUserIdentityStore.class);
    factory.register(IdentityStore.class, MemJwtIdentityStore.class);

    factory.register(CompressHandler.class, GzipCompressHandler.class);
    factory.register(CompressHandlerProvider.class, MockCompressHandlerProvider.class);
    factory.register(ActionFilterProvider.class, ActionFilterProvider.class);

    factory.register(RemMeTokenManager.class, DefaultRemMeTokenManager.class);
    factory.register(RemMeTokenHandler.class, DefaultRemMeTokenHandler.class);

    factory.register(ClientUrlInfoParser.class, DefaultClientUrlInfoParser.class);
    factory.register(InitialRequestGuard.class, DefaultInitialRequestGuard.class);

    factory.register(ConverterProvider.class, ConverterProvider.class);
    factory.register(LanguageProvider.class, MockLanguageProvider.class);
    factory.register(FormatProviderFactory.class, DefaultFormatProviderFactory.class);

    factory.register(ResourcesProvider.class, MockResourcesProvider.class);
    factory.register(RequestContextParser.class, RequestContextParser.class);
    factory.register(ConstDescProvider.class, ConstDescProvider.class);
    factory.register(GroupFormatProvider.class, GroupFormatProvider.class);

    factory.register(AppPolicyProvider.class, AppPolicyProvider.class);
    factory.register(CorsPolicyHandler.class, CorsPolicyHandler.class);

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
