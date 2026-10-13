// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.appslandia.common.base.InitializingObject;

/**
 *
 * @author Loc Ha
 *
 */
public class HeaderPolicyProvider extends InitializingObject {

  private List<HeaderPolicy> headerPolicies = new ArrayList<>();

  @Override
  protected void init() {
    headerPolicies = Collections.unmodifiableList(headerPolicies);
  }

  public HeaderPolicyProvider registerHttpPolicy(HeaderPolicy policy) {
    assertNotInitialized();
    headerPolicies.add(policy);
    return this;
  }

  public List<HeaderPolicy> getHeaderPolicies() {
    initialize();
    return headerPolicies;
  }
}
