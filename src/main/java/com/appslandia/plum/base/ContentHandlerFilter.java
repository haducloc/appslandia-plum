// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;

import com.appslandia.common.base.UncheckedException;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ContentHandlerFilter extends HttpFilter {
  private static final long serialVersionUID = 1L;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected MemoryStreamFactory memoryStreamFactory;

  @Inject
  protected CompressHandlerProvider compressHandlerProvider;

  @Inject
  protected ContentHandlerUtil contentHandlerUtil;

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Asserts.isTrue(
        request.getDispatcherType() == DispatcherType.REQUEST || request.getDispatcherType() == DispatcherType.ASYNC);

    try {
      // DEBUG
      if (appConfig.isEnableDebug()) {
        ServletUtils.testErrorStatus(request, "__test_error_status2");
      }

      // RequestContext
      var requestContext = ServletUtils.getRequestContext(request);
      var actionDesc = requestContext.getActionDesc();

      // ETag/Compression handling:
      //
      // REQUEST dispatch:
      // --- EnableAsync -> Skip (ExecutorServlet handles it)
      // --- Sync -> Handle
      //
      // ASYNC dispatch (view dispatches only occur from ExecutorServlet):
      // --- Sync -> Skip
      // --- Async + View -> Handle
      // --- Async + Non-View -> Skip (ExecutorServlet handles it)

      if ((request.getDispatcherType() == DispatcherType.REQUEST && actionDesc.getEnableAsync() != null)
          || (request.getDispatcherType() == DispatcherType.ASYNC && actionDesc.getEnableAsync() == null)) {
        chain.doFilter(request, response);
        return;
      }

      // ETAG
      if (requestContext.isGetOrHead() && actionDesc.getEnableEtag() != null) {
        doEtagRequest(request, response, requestContext, chain);
        return;
      }

      // Compression
      var compressHandler = (actionDesc.getEnableCompress() != null) ? compressHandlerProvider.getHandler(request)
          : null;
      if (compressHandler != null) {
        doCompressRequest(request, response, chain, compressHandler);
        return;
      }

      // Next filter
      chain.doFilter(request, response);

    } catch (RuntimeException | ServletException | IOException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new UncheckedException(ex);
    }
  }

  protected void doEtagRequest(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext,
      FilterChain chain) throws Exception {
    final var content = memoryStreamFactory.newMemoryStream();
    try {
      var wrapper = new MemoryResponseWrapper(response, true, content);
      chain.doFilter(request, wrapper);

      contentHandlerUtil.finishEtag(request, response, requestContext, wrapper);

    } finally {
      content.reset();
    }
  }

  protected void doCompressRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      CompressHandler compressHandler) throws Exception {
    var content = memoryStreamFactory.newMemoryStream();
    var wrapper = compressHandler.toCompressWrapper(response, content);
    try {
      chain.doFilter(request, wrapper);

      if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {
        return;
      }
      wrapper.finishResponse();

    } catch (Exception ex) {
      if (response.isCommitted()) {
        wrapper.finishResponse();
      }
      throw ex;

    } finally {
      content.reset();
    }
  }
}
