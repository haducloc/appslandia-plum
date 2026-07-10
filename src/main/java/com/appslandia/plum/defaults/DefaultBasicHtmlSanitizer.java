// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import com.appslandia.plum.base.BasicHtmlSanitizer;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultBasicHtmlSanitizer implements BasicHtmlSanitizer {

  //@formatter:off
  private static final PolicyFactory BASIC_HTML_POLICY = new HtmlPolicyBuilder()
      .allowElements("b", "strong", "i", "em", "p", "br", "a")
      .allowAttributes("href").onElements("a")
      .allowStandardUrlProtocols()
      .requireRelNofollowOnLinks()
      .toFactory();
  //@formatter:on

  @Override
  public String sanitize(String html) {
    if (html == null) {
      return "";
    }
    return BASIC_HTML_POLICY.sanitize(html);
  }
}
