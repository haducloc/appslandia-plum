// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * Each module must provide an implementation of this interface as an {@link ApplicationScoped} CDI bean annotated with
 * {@link Named} using the module name.
 *
 * @author Loc Ha
 */
public interface OAuth2AccountService {

  OAuth2Account getByEmail(String email);

  OAuth2Account getBySubject(String subject, String provider);

  void link(String email, String subject, String provider);
}
