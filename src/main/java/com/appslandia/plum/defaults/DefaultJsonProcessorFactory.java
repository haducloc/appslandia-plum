// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.jose.JoseJsonb;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.json.JsonbProcessor;
import com.appslandia.plum.base.AppConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultJsonProcessorFactory implements CDIFactory<JsonProcessor> {

  @Inject
  protected AppConfig appConfig;

  @Produces
  @ApplicationScoped
  @Override
  public JsonProcessor produce() {
    var prettyPrinting = appConfig.getBool(AppConfig.CONFIG_ENABLE_JSON_PRETTY);
    return createJsonbProcessor(true, prettyPrinting);
  }

  @Override
  public void dispose(@Disposes JsonProcessor impl) {
    impl.destroy();
  }

  @Produces
  @ApplicationScoped
  @Json(Profile.COMPACT)
  public JsonProcessor produceCompact() {
    return createJsonbProcessor(true, false);
  }

  public void disposeCompact(@Disposes @Json(Profile.COMPACT) JsonProcessor impl) {
    impl.destroy();
  }

  @Produces
  @ApplicationScoped
  @Json(Profile.PRETTY)
  public JsonProcessor producePretty() {
    return createJsonbProcessor(true, true);
  }

  public void disposePretty(@Disposes @Json(Profile.PRETTY) JsonProcessor impl) {
    impl.destroy();
  }

  static JsonbProcessor createJsonbProcessor(boolean serializeNulls, boolean prettyPrinting) {
    var config = JoseJsonb.newJsonbConfig(serializeNulls, prettyPrinting);
    return new JsonbProcessor().setConfig(config);
  }
}
