// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import com.appslandia.common.base.UncheckedException;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.plum.utils.ParamUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ExecutorServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Inject
  protected JsonProcessor jsonProcessor;

  @Inject
  protected ActionInvoker actionInvoker;

  @Inject
  protected ControllerProvider controllerProvider;

  @Inject
  protected CsrfManager csrfManager;

  @Inject
  protected TempDataManager tempDataManager;

  @Inject
  protected MemoryStreamFactory memoryStreamFactory;

  @Inject
  protected CompressHandlerProvider compressHandlerProvider;

  @Inject
  protected ContentHandlerUtil contentHandlerUtil;

  protected void doRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws ServletException, IOException {
    try {
      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status3");
      }
      var actionDesc = requestContext.getActionDesc();

      // TempData
      if (requestContext.isGetOrHead()) {
        tempDataManager.loadTempData(request, response);
      }

      // CSRF
      if ((actionDesc.getEnableCsrf() != null) && request.getMethod().equals(HttpMethod.POST)) {
        csrfManager.verifyCsrf(request, true);
      }
      var enableAsync = actionDesc.getEnableAsync();

      // SYNC
      if (enableAsync == null) {
        doInvokeAction(request, response, requestContext);

      } else {

        // dispatchToView
        if (enableAsync.dispatchToView()) {
          doDispatchToViewRequest(request, response, requestContext);
          return;
        }

        // ETAG
        if (requestContext.isGetOrHead() && actionDesc.getEnableEtag() != null) {
          doEtagRequest(request, response, requestContext);
          return;
        }

        // Compression
        var compressHandler = (actionDesc.getEnableCompress() != null) ? compressHandlerProvider.getHandler(request)
            : null;
        if (compressHandler != null) {
          doCompressRequest(request, response, requestContext, compressHandler);
          return;
        }

        // Other
        doOtherRequest(request, response, requestContext);
      }

    } catch (RuntimeException | ServletException | IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  protected void initAsyncContext(HttpServletRequest request, RequestContext requestContext,
      AsyncContext asyncContext) {
    var testTimeout = appConfig.isEnableDebug() ? getTestAsyncTimeout(request) : 0L;
    var timeout = (testTimeout > 0) ? testTimeout : requestContext.getActionDesc().getEnableAsync().timeout();

    if (timeout > 0) {
      asyncContext.setTimeout(timeout);
    }

    asyncContext.addListener(new AsyncListener() {

      @Override
      public void onTimeout(AsyncEvent event) throws IOException {
        requestContext.markAsyncTimedOut();

        try {
          var response = (HttpServletResponse) event.getSuppliedResponse();
          if (!response.isCommitted()) {

            var message = requestContext.res(Resources.ERROR_ASYNC_TIMED_OUT);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, message);
          }

        } finally {
          completeAsync(event.getAsyncContext());
        }
      }

      @Override
      public void onStartAsync(AsyncEvent event) throws IOException {
      }

      @Override
      public void onError(AsyncEvent event) throws IOException {
      }

      @Override
      public void onComplete(AsyncEvent event) throws IOException {
      }
    });
  }

  /**
   * ETag/Compression is handled by InitializerFilter during ASYNC dispatches to a view.
   *
   * Note: ViewHandler performs the dispatch to the view when executing in an ASYNC context.
   */
  protected void doDispatchToViewRequest(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext) throws Exception {
    requestContext.initAsyncTimedOut();
    var asyncContext = request.startAsync(request, response);
    initAsyncContext(request, requestContext, asyncContext);

    asyncContext.start(() -> {
      try {
        doInvokeAction(request, response, requestContext);

        var statusWrapper = ServletUtils.unwrap(response, HttpResponseWrapper.class);
        Asserts.notNull(statusWrapper);

        // 3XX
        if (300 <= statusWrapper.getTrackedStatus() && statusWrapper.getTrackedStatus() < 400) {
          completeAsync(asyncContext);
          return;
        }

      } catch (Exception ex) {
        if (!requestContext.isAsyncTimedOut()) {
          try {
            exceptionHandler.handleException(request, response, ex);
          } catch (Exception ignored) {
          }
        }
        completeAsync(asyncContext);

      } finally {
        // Do not complete after successful dispatch().
      }
    });
  }

  protected void doOtherRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    requestContext.initAsyncTimedOut();
    var asyncContext = request.startAsync(request, response);
    initAsyncContext(request, requestContext, asyncContext);

    asyncContext.start(() -> {
      try {
        doInvokeAction(request, response, requestContext);

      } catch (Exception ex) {
        if (!requestContext.isAsyncTimedOut()) {
          try {
            exceptionHandler.handleException(request, response, ex);
          } catch (Exception ignored) {
          }
        }

      } finally {
        completeAsync(asyncContext);
      }
    });
  }

  protected void doEtagRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    var content = memoryStreamFactory.newMemoryStream();
    var wrapper = new MemoryResponseWrapper(response, true, content);

    requestContext.initAsyncTimedOut();
    var asyncContext = request.startAsync(request, wrapper);
    initAsyncContext(request, requestContext, asyncContext);

    asyncContext.start(() -> {
      try {
        doInvokeAction(request, wrapper, requestContext);

        if (!requestContext.isAsyncTimedOut()) {
          contentHandlerUtil.finishEtag(request, response, requestContext, wrapper);
        }

      } catch (Exception ex) {
        if (!requestContext.isAsyncTimedOut()) {
          try {
            exceptionHandler.handleException(request, response, ex);
          } catch (Exception ignored) {
          }
        }

      } finally {
        completeAsync(asyncContext);
        content.reset();
      }
    });
  }

  protected void doCompressRequest(HttpServletRequest request, HttpServletResponse response,
      RequestContext requestContext, CompressHandler compressHandler) throws Exception {
    var content = memoryStreamFactory.newMemoryStream();
    var wrapper = compressHandler.toCompressWrapper(response, content);

    requestContext.initAsyncTimedOut();
    var asyncContext = request.startAsync(request, wrapper);
    initAsyncContext(request, requestContext, asyncContext);

    asyncContext.start(() -> {
      try {
        doInvokeAction(request, wrapper, requestContext);

        var statusWrapper = ServletUtils.unwrap(response, HttpResponseWrapper.class);
        Asserts.notNull(statusWrapper);

        // 3XX
        if (300 <= statusWrapper.getTrackedStatus() && statusWrapper.getTrackedStatus() < 400) {
          return;
        }

        try {
          wrapper.finishResponse();
        } catch (Exception ignored) {
          // Best effort only. Response may already be closed after timeout.
        }

      } catch (Exception ex) {

        if (isCommittedSafe(response)) {
          try {
            wrapper.finishResponse();
          } catch (Exception ignored) {
          }
        }

        if (!requestContext.isAsyncTimedOut()) {
          try {
            exceptionHandler.handleException(request, response, ex);
          } catch (Exception ignored) {
          }
        }

      } finally {
        completeAsync(asyncContext);
        content.reset();
      }
    });
  }

  protected void doInvokeAction(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {

    // DEBUG
    if (appConfig.isEnableDebug()) {
      ServletUtils.testErrorStatus(request, "__test_error_status4");
    }

    var actionDesc = requestContext.getActionDesc();
    var controller = controllerProvider.getController(actionDesc.getControllerClass());

    // Test Async-Timeout
    if (appConfig.isEnableDebug()) {
      if (actionDesc.getEnableAsync() != null) {

        var testAsyncTimeout = getTestAsyncTimeout(request);
        if (testAsyncTimeout > 0) {
          Thread.sleep(testAsyncTimeout + 1500);
        }
      }
    }

    var result = actionInvoker.invokeAction(request, response, requestContext, controller);
    if (result == null) {
      return;
    }

    if (ActionResult.class.isAssignableFrom(actionDesc.getMethod().getReturnType())) {
      ((ActionResult) result).execute(request, response, requestContext);

    } else {
      writeJsonResult(response, result);
    }

    // DEBUG
    if (appConfig.isEnableDebug()) {
      ServletUtils.testErrorStatus(request, "__test_error_status5");
    }
  }

  protected void writeJsonResult(HttpServletResponse response, Object result) throws Exception {
    response.setContentType(ContentTypes.APP_JSON);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    jsonProcessor.write(response.getWriter(), result);
  }

  private static long getTestAsyncTimeout(HttpServletRequest request) {
    var asyncTimeoutSec = ParamUtils.getLong(request, "__test_async_timeout");
    return TimeUnit.SECONDS.toMillis(asyncTimeoutSec);
  }

  private static boolean isCommittedSafe(HttpServletResponse response) {
    try {
      return response.isCommitted();

    } catch (IllegalStateException ex) {
      // GlassFish: Internal org.glassfish.grizzly.http.server.Response has not been set
      return false;
    }
  }

  private static void completeAsync(AsyncContext ac) {
    try {
      ac.complete();
    } catch (IllegalStateException ignored) {
    }
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (request.getMethod().equals(HttpMethod.PATCH)) {
      doPatch(request, response);
    } else {
      super.service(request, response);
    }
  }

  @Override
  protected void doPatch(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doRequest(request, response, ServletUtils.getRequestContext(request));
  }

  @Override
  protected void doOptions(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }
}
