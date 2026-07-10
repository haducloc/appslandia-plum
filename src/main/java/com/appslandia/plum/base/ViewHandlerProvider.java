// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ViewHandlerProvider extends InitializingObject {

  private Map<String, ViewHandler> viewHandlerMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    var viewHandlerMap = this.viewHandlerMap;

    viewHandlerMap.putIfAbsent(".jsp", new DefaultViewHandler());
    viewHandlerMap.putIfAbsent(".xhtml", new DefaultViewHandler());

    this.viewHandlerMap = Collections.unmodifiableMap(viewHandlerMap);
  }

  public ViewHandler getViewHandler(String suffix) {
    initialize();
    var impl = viewHandlerMap.get(suffix);
    return Arguments.notNull(impl, "No ViewHandler is registered for suffix '{}'.", suffix);
  }

  public void registerViewHandler(String viewSuffix, ViewHandler impl) {
    assertNotInitialized();
    viewHandlerMap.put(viewSuffix, impl);
  }

  public Set<String> getViewSuffixes() {
    initialize();
    return viewHandlerMap.keySet();
  }

  //@formatter:off
  @Override
  public String toString() {
    initialize();

    var desc = new TextBuilder(512);
    desc.append(ObjectUtils.toIdHash(this)).append("[").appendln();

    for (var handler : viewHandlerMap.entrySet()) {
      desc.appendsp(2).append(ObjectUtils.toIdHash(handler.getValue())).append("(").appendln()
          .appendsp(4).append("viewSuffix: ").append(handler.getKey()).appendln()
          .appendsp(2).appendln(")");
    }
    desc.append("]").appendln();
    return desc.toString();
  }
  // @formatter:on

  static class DefaultViewHandler implements ViewHandler {

    @Override
    public void execute(String viewLocation, Map<String, Object> model, HttpServletRequest request,
        HttpServletResponse response, RequestContext requestContext) throws Exception {
      Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST);

      if (model != null) {
        for (Map.Entry<String, Object> variable : model.entrySet()) {
          request.setAttribute(variable.getKey(), variable.getValue());
        }
      }

      // ASYNC
      if (requestContext.getActionDesc().getEnableAsync() != null) {
        Asserts.isTrue(request.isAsyncStarted(), "The request is not in an active asynchronous context.");

        // dispatch to view
        request.getAsyncContext().dispatch(viewLocation);
        return;
      }

      // SYNC: forward to view
      var requestDispatcher = ServletUtils.getRequestDispatcher(request, viewLocation);
      requestDispatcher.forward(request, response);
    }
  }
}
