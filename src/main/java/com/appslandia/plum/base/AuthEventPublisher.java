// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AuthEventPublisher {

  @Inject
  protected Event<AuthEvent> event;

  public void fire(AuthEvent authEvent) {
    event.fire(authEvent);
  }

  public static AuthEvent newBaseAuthEvent(HttpServletRequest request) {
    var event = new AuthEvent();
    var requestContext = ServletUtils.getRequestContext(request);

    event.setClientIp(requestContext.getClientAddress());
    event.setUserAgent(ServletUtils.getNormUserAgent(request));
    event.setRequestId(request.getRequestId());

    var sessionIdDigester = ServletUtils.getAppScoped(request, HttpSessionIdDigester.class);
    var sessionIdDigest = sessionIdDigester.getSessionIdDigest(request);
    event.setSessionId(sessionIdDigest);

    return event;
  }
}
