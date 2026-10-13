// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.Language;

/**
 *
 * @author Loc Ha
 *
 */
public interface FormatProviderFactory {

  FormatProvider produce(Language language);
}
