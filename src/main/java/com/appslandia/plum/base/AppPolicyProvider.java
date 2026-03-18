// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
public class AppPolicyProvider extends InitializingObject {

  private List<CorsPolicy> corsPolicies = new ArrayList<>();
  private List<AuthorizePolicy> authorizePolicies = new ArrayList<>();

  private List<EtagPolicy> etagPolicies = new ArrayList<>();
  private List<CompressPolicy> compressPolicies = new ArrayList<>();
  private List<HeaderPolicy> headerPolicies = new ArrayList<>();

  @Override
  protected void init() {
    corsPolicies = Collections.unmodifiableList(corsPolicies);
    authorizePolicies = Collections.unmodifiableList(authorizePolicies);

    etagPolicies = Collections.unmodifiableList(etagPolicies);
    compressPolicies = Collections.unmodifiableList(compressPolicies);
    headerPolicies = Collections.unmodifiableList(headerPolicies);
  }

  public AppPolicyProvider registerPolicy(HeaderPolicy policy) {
    assertNotInitialized();
    headerPolicies.add(policy);
    return this;
  }

  public AppPolicyProvider registerPolicy(CorsPolicy policy) {
    assertNotInitialized();
    corsPolicies.add(policy);
    return this;
  }

  public AppPolicyProvider registerPolicy(EtagPolicy policy) {
    assertNotInitialized();
    etagPolicies.add(policy);
    return this;
  }

  public AppPolicyProvider registerPolicy(CompressPolicy policy) {
    assertNotInitialized();
    compressPolicies.add(policy);
    return this;
  }

  public List<CorsPolicy> getCorsPolicies() {
    initialize();
    return corsPolicies;
  }

  public List<AuthorizePolicy> getAuthorizePolicies() {
    initialize();
    return authorizePolicies;
  }

  public List<EtagPolicy> getEtagPolicies() {
    initialize();
    return etagPolicies;
  }

  public List<CompressPolicy> getCompressPolicies() {
    initialize();
    return compressPolicies;
  }

  public List<HeaderPolicy> getHeaderPolicies() {
    initialize();
    return headerPolicies;
  }
}
