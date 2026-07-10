// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.Instant;
import java.util.Map;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;

import jakarta.security.enterprise.CallerPrincipal;

/**
 *
 * @author Loc Ha
 *
 */
public class AuthPrincipal extends CallerPrincipal {
  private static final long serialVersionUID = 1L;

  // https://www.iana.org/assignments/jwt/jwt.xhtml#claims

  public static final String ATTRIBUTE_SUB = "sub";
  public static final String ATTRIBUTE_DISPLAY_NAME = "name";
  public static final String ATTRIBUTE_ROLES = "roles";

  public static final String ATTRIBUTE_EMAIL = "email";
  public static final String ATTRIBUTE_EMAIL_VERIFIED = "email_verified";

  public static final String ATTRIBUTE_PHONE_NUMBER = "phone_number";
  public static final String ATTRIBUTE_PHONE_NUMBER_VERIFIED = "phone_number_verified";

  // Custom claims
  public static final String ATTRIBUTE_LDAP_DN = "ldap_dn";
  public static final String ATTRIBUTE_USERNAME = "username";

  final String storeId;
  final String module;
  final Map<String, Object> attributes;

  public AuthPrincipal(String storeId, String module, String callerName, Map<String, Object> attributes) {
    super(callerName);

    this.storeId = storeId;
    this.module = module;

    this.attributes = CollectionUtils.toUnmodifiableMap(attributes);
  }

  public String getDisplayName() {
    var val = (String) get(ATTRIBUTE_DISPLAY_NAME);
    return (val != null) ? val : getName();
  }

  public Object get(String attributeName) {
    return attributes.get(attributeName);
  }

  public Object getReq(String attributeName) {
    var val = attributes.get(attributeName);
    return Asserts.notNull(val, "The given attribute '{}' is required.", attributeName);
  }

  public String getStoreId() {
    return storeId;
  }

  public String getModule() {
    return module;
  }

  // Internal override only

  public boolean isRememberMe() {
    throw new UnsupportedOperationException();
  }

  // Internal override only

  public Instant getReauthAt() {
    throw new UnsupportedOperationException();
  }

  public String getCallerUniqueId() {
    var id = get(ATTRIBUTE_SUB);
    if (id != null) {
      return id.toString();
    }
    id = get(ATTRIBUTE_USERNAME);
    if (id != null) {
      return id.toString();
    }
    return getName();
  }
}
