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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.results.JspResult;
import com.appslandia.plum.utils.ServletUtils;
import com.appslandia.plum.utils.WebBeanToStringDecision;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ErrorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String CONFIG_ERROR_DEV = ErrorServlet.class.getName() + ".error_dev";

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected ExceptionHandler exceptionHandler;

    @Inject
    protected RequestContextParser requestContextParser;

    @Inject
    @Json(Profile.PRETTY)
    protected JsonProcessor jsonProcessor;

    private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	if (request.getDispatcherType() == DispatcherType.REQUEST) {
	    this.exceptionHandler.writeSimpleHtml(request, response, HttpServletResponse.SC_NOT_FOUND, "404: Not Found");
	    return;
	}
	Asserts.isTrue(request.getDispatcherType() == DispatcherType.ERROR);

	if (this.appConfig.getBool(CONFIG_ERROR_DEV, false)) {
	    writeErrorDev(request, response);
	} else {
	    writeErrorProd(request, response);
	}
    }

    protected void writeErrorProd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	RequestContext requestContext = this.requestContextParser.parse(request, response);
	Problem problem = (Problem) request.getAttribute(Problem.class.getName());

	if (problem == null) {
	    Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
	    problem = this.exceptionHandler.getProblem(request, requestContext, exception);
	}
	Asserts.notNull(problem.getStatus());
	Asserts.notNull(problem.getTitle());

	request.setAttribute(Problem.class.getName(), problem);
	final ResponseSafeWriter responseWrapper = new ResponseSafeWriter(response);

	try {
	    String errorJspPath = JspResult.getJspPath(request, getErrorJsp(request, requestContext));
	    ServletUtils.forward(request, responseWrapper, errorJspPath);

	    responseWrapper.flushBuffer();

	} catch (Exception ex) {
	    if (!response.isCommitted()) {
		this.exceptionHandler.writeSimpleHtml(request, response, problem.getStatus(), problem.getTitle());
	    }
	}
    }

    protected String getErrorJsp(HttpServletRequest request, RequestContext requestContext) {
	return "/error.jsp";
    }

    protected void writeErrorDev(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setContentType(MimeTypes.TEXT_PLAIN);
	response.setCharacterEncoding(StandardCharsets.UTF_8.name());

	PrintWriter out = ServletUtils.getPrintWriter(response);

	out.append("Error Exception: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)));
	out.println();
	out.append("Error Exception Type: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE)));
	out.println();
	out.append("Error Message: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)));
	out.println();
	out.append("Error Request URI: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)));
	out.println();
	out.append("Error Servlet Name: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME)));
	out.println();
	out.append("Error Status Code: ").append(String.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)));
	out.println();

	RequestContext requestContext = this.requestContextParser.parse(request, response);
	Problem problem = (Problem) request.getAttribute(Problem.class.getName());

	if (problem == null) {
	    Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
	    problem = this.exceptionHandler.getProblem(request, requestContext, exception);
	}
	Asserts.notNull(problem.getStatus());
	Asserts.notNull(problem.getTitle());

	out.println();
	out.append("Problem Status: ").append(String.valueOf(problem.getStatus()));
	out.println();
	out.append("Problem Title: ").append(problem.getTitle());
	out.println();
	out.append("Problem Detail: ").append(problem.getDetail());
	out.println();
	out.append("Problem Type: ").append(problem.getType());
	out.println();
	out.append("Problem Instance: ").append(problem.getInstance());
	out.println();
	out.append("Problem ModelState: ").append(new ToStringBuilder(3).toString(problem.getModelState()));
	out.println();
	out.append("Problem Extensions: ").append(new ToStringBuilder().toString(problem.getExtensions()));
	out.println();

	if (problem.getException() != null) {
	    out.println();

	    out.append("Problem Exception: ");
	    problem.getException().printStackTrace(out);
	    out.println();
	}
	out.println();
	out.append("Request Info: ").append(new ToStringBuilder(4).toStringDecision(new WebBeanToStringDecision(true, true, true, false)).toString(request));
	out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doRequest(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doRequest(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doRequest(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doRequest(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	throw new UnsupportedOperationException();
    }
}
