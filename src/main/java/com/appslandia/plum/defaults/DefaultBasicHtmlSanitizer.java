// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.BasicHtmlSanitizer;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultBasicHtmlSanitizer implements BasicHtmlSanitizer {

  public static final String CONFIG_ALLOWED_RESOURCE_DOMAINS = "html_sanitizer.allowed_resource_domains";

  protected Set<String> allowedResourceDomains;

  protected PolicyFactory basicHtmlPolicy;

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @PostConstruct
  protected void initialize() {
    var allowedDomains = appConfig.getStringArray(CONFIG_ALLOWED_RESOURCE_DOMAINS);
    allowedResourceDomains = Arrays.stream(allowedDomains).map(domain -> domain.toLowerCase(Locale.ROOT))
        .collect(Collectors.toUnmodifiableSet());

    appLogger.info("{}: {}", CONFIG_ALLOWED_RESOURCE_DOMAINS, String.join(", ", allowedResourceDomains));

    //@formatter:off
    basicHtmlPolicy = new HtmlPolicyBuilder()
        .allowElements(
            "p", "br", "div",
            "h1", "h2", "h3", "h4", "h5", "h6",
            "b", "strong", "i", "em", "u", "s", "del",
            "ul", "ol", "li",
            "blockquote",
            "table", "thead", "tbody", "tfoot", "tr", "th", "td", "caption",
            "a", "img"
        )
        .allowAttributes("href")
            .matching(this::sanitizeUrl)
            .onElements("a")
        .allowAttributes("src")
            .matching(this::sanitizeUrl)
            .onElements("img")
        .allowAttributes("alt")
            .onElements("img")
        .allowAttributes("scope")
            .matching(true, "row", "col", "rowgroup", "colgroup")
            .onElements("th")
        .allowUrlProtocols("https")
        .requireRelNofollowOnLinks()
        .toFactory();
    //@formatter:on
  }

  private String sanitizeUrl(String elementName, String attributeName, String value) {
    try {
      var uri = URI.create(value);

      // Allow normal relative URLs.
      if (!uri.isAbsolute() && uri.getRawAuthority() == null) {
        return value;
      }

      // Absolute URLs must use HTTPS.
      if (!"https".equalsIgnoreCase(uri.getScheme())) {
        return null;
      }

      var host = uri.getHost();
      if (host == null) {
        return null;
      }

      if (allowedResourceDomains.contains(host.toLowerCase(Locale.ROOT))) {
        return value;
      }
    } catch (IllegalArgumentException ex) {
      // Invalid URI.
    }
    return null;
  }

  @Override
  public String sanitize(String html) {
    if (html == null) {
      return "";
    }
    return basicHtmlPolicy.sanitize(html);
  }
}
