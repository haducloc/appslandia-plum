// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.FormatProviderImpl;
import com.appslandia.common.base.Language;
import com.appslandia.plum.base.FormatProviderFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultFormatProviderFactory implements FormatProviderFactory {

  @Override
  public FormatProvider produce(Language language) {
    return new FormatProviderImpl(language);
  }
}
