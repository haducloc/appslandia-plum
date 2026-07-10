// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.base.InitializingException;
import com.appslandia.common.base.SimpleConfig;
import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.SYS;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAppConfigFactory implements CDIFactory<AppConfig> {

  public static final String CONFIG_FILE = "config.properties";
  public static final String ENV_CONFIG_FILE = "config.{}.properties";

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected ServletContext servletContext;

  @Produces
  @ApplicationScoped
  @Override
  public AppConfig produce() {
    try {
      var config = new SimpleConfig();

      // -DconfigFile
      var configFile = SYS.getProp("configFile");

      if (configFile == null) {
        appLogger.info("System property 'configFile' not found. Loading WEB-INF/config.properties.");
        loadWebInfConfigs(config);

      } else {
        appLogger.info("System property 'configFile' found. Loading external configuration file.");
        loadExternalConfigs(config, configFile);
      }
      return new AppConfig(config);

    } catch (IOException ex) {
      throw new InitializingException(ex);
    }
  }

  protected void loadWebInfConfigs(SimpleConfig config) throws IOException {
    var configPath = "/WEB-INF/" + CONFIG_FILE;

    appLogger.info("Loading configs from " + configPath);
    if (!loadProps(configPath, config, servletContext)) {
      throw new InitializingException("Config file not found: " + configPath);
    }

    var envConfigPath = "/WEB-INF/" + STR.fmt(ENV_CONFIG_FILE, DeployEnv.getCurrent().getName());
    appLogger.info("Trying to load configs from " + envConfigPath);

    if (!loadProps(envConfigPath, config, servletContext)) {
      appLogger.info("Optional config file not found: " + envConfigPath);
    }
  }

  protected void loadExternalConfigs(SimpleConfig config, String configFile) throws IOException {
    // config.properties
    var cfgFile = Path.of(configFile).toAbsolutePath().normalize();

    if (!Files.isRegularFile(cfgFile)) {
      throw new InitializingException("Config file not found: " + cfgFile);
    }

    if (!CONFIG_FILE.equals(cfgFile.getFileName().toString())) {
      throw new InitializingException(
          STR.fmt("Unsupported config file name: {}. Expected: {}", cfgFile.getFileName(), CONFIG_FILE));
    }

    appLogger.info("Loading configs from " + cfgFile);
    loadProps(cfgFile, config);

    // config.ENV.properties
    var envFileName = STR.fmt(ENV_CONFIG_FILE, DeployEnv.getCurrent().getName());
    var envConfigFile = cfgFile.getParent().resolve(envFileName);

    appLogger.info("Trying to load configs from " + envConfigFile);

    if (Files.isRegularFile(envConfigFile)) {
      loadProps(envConfigFile, config);

    } else {
      appLogger.info("Optional config file not found: " + envConfigFile);
    }
  }

  static void loadProps(Path file, SimpleConfig config) throws IOException {
    try (var in = Files.newInputStream(file)) {
      config.load(in);
    }
  }

  static boolean loadProps(String resourcePath, SimpleConfig config, ServletContext sc) throws IOException {
    var in = sc.getResourceAsStream(resourcePath);
    if (in == null) {
      return false;
    }
    try {
      config.load(in);
      return true;

    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  @Override
  public void dispose(@Disposes AppConfig impl) {
  }
}
