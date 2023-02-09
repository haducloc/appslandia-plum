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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.logging.AppLogger;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ExecutorHandler extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    protected JsonProcessor jsonProcessor;

    @Inject
    protected HeaderPolicyProvider headerPolicyProvider;

    @Inject
    protected ActionInvoker actionInvoker;

    @Inject
    protected ControllerProvider controllerProvider;

    @Inject
    protected CaptchaManager captchaManager;

    @Inject
    protected CsrfManager csrfManager;

    @Inject
    protected TempDataManager tempDataManager;

    @Inject
    protected ActionFilterProvider actionFilterProvider;

    @Inject
    protected AppLogger appLogger;

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected ExceptionHandler exceptionHandler;

    protected ExecutorService getAsyncExecutor() {
	return null;
    }

    protected void testErrorStatus(HttpServletRequest request) {
	String statusValue = request.getParameter("__error_status");
	if (StringUtils.isNullOrEmpty(statusValue)) {
	    return;
	}

	int status = Integer.parseInt(statusValue);
	if (status >= 400 && status < 600) {

	    throw new HttpException(status);
	} else {
	    throw new BadRequestException("__error_status is invalid.");
	}
    }

    protected void doRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws ServletException, IOException {
	if (this.appConfig.isEnableDebug()) {
	    testErrorStatus(request);
	}
	final EnableAsync enableAsync = requestContext.getActionDesc().getEnableAsync();

	// Not startAsync
	if ((enableAsync == null) || enableAsync.markOnly()) {
	    try {
		getFilterChain(requestContext).doFilter(request, response, requestContext);

	    } catch (RuntimeException | ServletException | IOException ex) {
		throw ex;
	    } catch (Exception ex) {
		throw new ServletException(ex);
	    }
	    return;
	}

	// startAsync
	final AsyncContext asyncContext = request.startAsync();

	if (enableAsync.asyncListener() != EnableAsync.NoAsyncListener.class) {
	    asyncContext.addListener(asyncContext.createListener(enableAsync.asyncListener()));
	}
	asyncContext.setTimeout(enableAsync.timeoutMs() > 0 ? enableAsync.timeoutMs() : this.appConfig.getRequiredLong(AppConfig.CONFIG_ASYNC_TIMEOUT_MS));

	final Runnable asyncTask = new Runnable() {

	    @Override
	    public void run() {
		HttpServletRequest asyncReq = (HttpServletRequest) asyncContext.getRequest();
		HttpServletResponse asyncResp = (HttpServletResponse) asyncContext.getResponse();
		try {
		    getFilterChain(requestContext).doFilter(asyncReq, asyncResp, requestContext);

		} catch (Exception ex) {
		    try {
			exceptionHandler.handleException(asyncReq, asyncResp, ex);
		    } catch (Exception t) {
			appLogger.error(t);
		    }
		} finally {

		    // Complete ASYNC?
		    if (!RequestFlags.isFlagSet(asyncReq, RequestFlags.FLAG_ASYNC_COMPLETE_IMPLICITLY)) {
			asyncContext.complete();
		    }
		}
	    }
	};
	if (getAsyncExecutor() == null) {
	    asyncContext.start(asyncTask);
	} else {
	    getAsyncExecutor().execute(asyncTask);
	}
    }

    protected static final String[] DEFAULT_FILTERS = { ContentFilterImpl.NAME };

    protected ActionFilterChain getFilterChain(RequestContext requestContext) {
	String[] actionFilters = (requestContext.getActionDesc().getEnableFilters() == null) ? DEFAULT_FILTERS
		: ArrayUtils.append(DEFAULT_FILTERS, requestContext.getActionDesc().getEnableFilters().value());

	return new ActionFilterChain(actionFilters, this.actionFilterProvider) {

	    @Override
	    protected void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
		onActionInvoking(request, response, requestContext);

		// Invoke Action
		Object controller = controllerProvider.getController(requestContext.getActionDesc().getControllerClass());
		Object result = actionInvoker.invokeAction(request, response, requestContext, controller);

		onResultExecuting(request, response, requestContext, result);

		// Execute Result
		if (ActionResult.class.isAssignableFrom(requestContext.getActionDesc().getMethod().getReturnType())) {
		    Asserts.notNull(result);
		    ((ActionResult) result).execute(request, response, requestContext);

		} else if (requestContext.getActionDesc().getMethod().getReturnType() != void.class) {
		    writeJsonResult(response, result);
		}
	    }
	};
    }

    protected void onActionInvoking(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
	// TempData
	if (requestContext.isGetOrHead()) {
	    this.tempDataManager.loadTempData(request, response);
	}
	// CSRF
	if ((requestContext.getActionDesc().getEnableCsrf() != null) && request.getMethod().equals(HttpMethod.POST)) {
	    this.csrfManager.verifyCsrf(request, true);
	}
	// CAPTCHA
	if ((requestContext.getActionDesc().getEnableCaptcha() != null) && request.getMethod().equals(HttpMethod.POST)) {
	    this.captchaManager.verifyCaptcha(request);
	}
    }

    protected void onResultExecuting(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext, Object result) throws Exception {
	// @CacheControl
	if ((requestContext.getActionDesc().getCacheControl() != null) && requestContext.isGetOrHead()) {
	    this.headerPolicyProvider.getHeaderPolicy(requestContext.getActionDesc().getCacheControl().value()).writePolicy(request, response, requestContext);
	}
    }

    protected void writeJsonResult(HttpServletResponse response, Object result) throws Exception {
	response.setContentType(MimeTypes.APP_JSON);
	response.setCharacterEncoding(StandardCharsets.UTF_8.name());
	this.jsonProcessor.write(response.getWriter(), result);
    }

    public boolean isMockContext() {
	return false;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	if (request.getMethod().equals(HttpMethod.PATCH)) {
	    doPatch(request, response);
	} else {
	    super.service(request, response);
	}
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	doRequest(request, response, ServletUtils.getRequestContext(request));
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	throw new UnsupportedOperationException();
    }
}
