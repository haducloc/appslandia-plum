// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonException;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.base.SeriesToken;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class OAuth2FlowDelegate {

  @Inject
  @Json(Profile.COMPACT)
  protected JsonProcessor jsonProcessor;

  @Inject
  protected OAuth2StateHandler stateHandler;

  @Inject
  protected OAuth2Service service;

  @Inject
  protected OAuth2ConfigProvider configProvider;

  protected OAuth2Config getConfig(String provider) {
    return configProvider.getConfig(provider);
  }

  public String buildAuthorizationUrl(HttpServletRequest request, String module, String provider, String callbackUri,
      String returnUrl) {

    Arguments.notNull(request);
    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(callbackUri);

    var config = getConfig(provider);

    var boundClientIp = config.isStateBoundClientIp(module);
    var boundUserAgent = config.isStateBoundUserAgent(module);
    var clientBoundData = getClientBoundData(request, boundClientIp, boundUserAgent);

    var codeVerifier = new Out<String>();
    var seriesToken = stateHandler.createState(module, provider, returnUrl, clientBoundData, codeVerifier);

    var state = buildState(seriesToken);
    return service.buildAuthorizationUrl(module, provider, callbackUri, state, codeVerifier.get());
  }

  public OAuth2Token handleCallback(HttpServletRequest request, String module, String provider, String state,
      String code, String callbackUri, Out<String> returnUrl) {

    Arguments.notNull(request);
    Arguments.notNull(module);
    Arguments.notNull(provider);
    Arguments.notNull(state);
    Arguments.notNull(code);
    Arguments.notNull(returnUrl);

    var config = getConfig(provider);
    var seriesToken = parseState(state);

    if (seriesToken == null || seriesToken.getSeries() == null || seriesToken.getToken() == null) {
      throw new OAuth2Exception("Invalid OAuth2 state.");
    }

    var boundClientIp = config.isStateBoundClientIp(module);
    var boundUserAgent = config.isStateBoundUserAgent(module);
    var clientBoundData = getClientBoundData(request, boundClientIp, boundUserAgent);

    var invalidCode = new Out<String>();
    var oauth2State = stateHandler.verifyState(module, provider, seriesToken.getSeries(), seriesToken.getToken(),
        clientBoundData, invalidCode);

    if (invalidCode.value != null) {
      throw new OAuth2Exception("Invalid OAuth2 state: " + invalidCode.value);
    }
    returnUrl.value = oauth2State.getReturnUrl();

    try {
      var token = service.exchangeCode(module, provider, callbackUri, code, oauth2State.getCodeVerifier());
      return jsonProcessor.read(token, OAuth2Token.class);

    } finally {
      stateHandler.remove(oauth2State.getSeries());
    }
  }

  public String getUserInfo(String module, String provider, String accessToken) {
    return service.getUserInfo(module, provider, accessToken);
  }

  protected String buildState(SeriesToken token) {
    return BaseEncoder.BASE64_URL_NP.encode(jsonProcessor.toString(token).getBytes(StandardCharsets.UTF_8));
  }

  protected SeriesToken parseState(String state) {
    try {
      return jsonProcessor.read(
          new StringReader(new String(BaseEncoder.BASE64_URL_NP.decode(state), StandardCharsets.UTF_8)),
          SeriesToken.class);

    } catch (JsonException ex) {
      return null;
    }
  }

  protected String getClientBoundData(HttpServletRequest request, boolean boundClientIp, boolean boundUserAgent) {
    return ServletUtils.getClientBoundData(request, boundClientIp, boundUserAgent);
  }
}
