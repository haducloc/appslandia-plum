// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.URLUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class OAuth2Service {

  private static final Set<String> RESERVED_AUTHORIZATION_PARAMETERS = Set.of("client_id", "redirect_uri",
      "response_type", "state", "scope", "code_challenge", "code_challenge_method");

  @Inject
  protected OAuth2ConfigProvider configProvider;

  protected OAuth2Config getConfig(String provider) {
    return configProvider.getConfig(provider);
  }

  public String buildAuthorizationUrl(String module, String provider, String callbackUri, String state,
      String codeVerifier) {

    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(callbackUri);
    Arguments.notNull(state);
    Arguments.notNull(codeVerifier);

    var config = getConfig(provider);
    var params = new LinkedHashMap<String, Object>();

    params.put("client_id", config.getClientId(module));
    params.put("redirect_uri", callbackUri);
    params.put("response_type", "code");
    params.put("state", state);
    params.put("scope", String.join(" ", config.getScope(module)));

    params.put("code_challenge_method", getCodeChallengeMethod());
    params.put("code_challenge", createCodeChallenge(codeVerifier));

    for (var entry : config.getAuthorizationParameters().entrySet()) {
      if (RESERVED_AUTHORIZATION_PARAMETERS.contains(entry.getKey())) {
        throw new OAuth2Exception("Reserved OAuth2 authorization parameter: " + entry.getKey());
      }
      params.put(entry.getKey(), entry.getValue());
    }

    return URLUtils.toUrl(config.getAuthorizationEndpoint(), params);
  }

  public String exchangeCode(String module, String provider, String callbackUri, String code, String codeVerifier)
      throws OAuth2Exception {

    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(callbackUri);
    Arguments.notNull(codeVerifier);

    var config = getConfig(provider);

    var params = new LinkedHashMap<String, Object>();
    var headers = new LinkedHashMap<String, String>();

    params.put("grant_type", "authorization_code");
    params.put("code", code);
    params.put("redirect_uri", callbackUri);
    params.put("code_verifier", codeVerifier);

    config.configureTokenRequest(module, params, headers);
    headers.putIfAbsent("Accept", "application/json");

    return postForm(config, config.getTokenEndpoint(), params, headers);
  }

  public String getUserInfo(String module, String provider, String accessToken) throws OAuth2Exception {
    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(accessToken);

    var config = getConfig(provider);

    var params = new LinkedHashMap<String, Object>();
    var headers = new LinkedHashMap<String, String>();

    params.putAll(config.getUserInfoParameters());

    config.configureUserInfoRequest(module, accessToken, params, headers);
    headers.putIfAbsent("Accept", "application/json");

    return get(config, config.getUserInfoEndpoint(), params, headers);
  }

  public String refreshToken(String module, String provider, String refreshToken) throws OAuth2Exception {
    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(refreshToken);

    var config = getConfig(provider);

    var params = new LinkedHashMap<String, Object>();
    var headers = new LinkedHashMap<String, String>();

    params.put("grant_type", "refresh_token");
    params.put("refresh_token", refreshToken);

    config.configureTokenRequest(module, params, headers);
    headers.putIfAbsent("Accept", "application/json");

    return postForm(config, config.getTokenEndpoint(), params, headers);
  }

  protected String getCodeChallengeMethod() {
    return "S256";
  }

  protected String createCodeChallenge(String codeVerifier) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      var bytes = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));

      return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    } catch (NoSuchAlgorithmException ex) {
      throw new Error("SHA-256 is not available.", ex);
    }
  }

  protected String get(OAuth2Config config, String url, Map<String, Object> params, Map<String, String> headers) {
    var requestUrl = URLUtils.toUrl(url, params);
    var builder = HttpRequest.newBuilder(URI.create(requestUrl)).GET();

    addHeaders(builder, headers);

    var response = send(config, builder.build());
    return response.body();
  }

  protected String postForm(OAuth2Config config, String url, Map<String, Object> params, Map<String, String> headers) {
    var body = URLUtils.toQueryParams(params);

    var builder = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

    addHeaders(builder, headers);

    var response = send(config, builder.build());
    return response.body();
  }

  protected HttpResponse<String> send(OAuth2Config config, HttpRequest request) {
    try {
      var httpClient = config.getHttpClient();
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new OAuth2HttpException(response.statusCode(), response.body());
      }
      return response;

    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new OAuth2Exception("OAuth2 HTTP request interrupted.", ex);

    } catch (IOException ex) {
      throw new OAuth2Exception("OAuth2 HTTP request failed.", ex);
    }
  }

  static void addHeaders(HttpRequest.Builder builder, Map<String, String> headers) {
    if (headers == null || headers.isEmpty()) {
      return;
    }
    for (var entry : headers.entrySet()) {
      builder.header(entry.getKey(), entry.getValue());
    }
  }
}
