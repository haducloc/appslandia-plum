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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.ObjectUtils;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.security.enterprise.identitystore.IdentityStore.ValidationType;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class IdentityStoreHandlerBase extends InitializingObject implements IdentityStoreHandler {

  protected List<IdentityStoreBase> identityStores = new ArrayList<>();

  @Override
  protected void init() throws Exception {
    // Validate identityStores
    var storeIds = new HashSet<String>();
    for (var store : identityStores) {
      if (!storeIds.add(store.storeId())) {
        throw new IllegalStateException("Duplicate storeId: " + store.storeId());
      }
    }

    Collections.sort(identityStores, (s1, s2) -> Integer.valueOf(s1.priority()).compareTo(s2.priority()));
    identityStores = Collections.unmodifiableList(identityStores);
  }

  @Override
  public CredentialValidationResult validate(Credential credential) {
    initialize();

    // AuthCredential
    if (!(credential instanceof AuthCredential authCredential)) {
      throw new IllegalArgumentException(
          "The given credential must be an instance of com.appslandia.plum.base.AuthCredential.");
    }

    // RULES:
    // - Ignore NOT_VALIDATED results.
    // - Stop at the first VALID result.
    // - Add groups from the VALID result.
    // - Aggregate more groups from stores that support PROVIDE_GROUPS.
    // - If all stores return NOT_VALIDATED, return NOT_VALIDATED.
    // - Otherwise, return the last non-NOT_VALIDATED result.

    CredentialValidationResult result = null;
    var validated = false;

    for (var store : identityStores) {
      if (!store.validationTypes().contains(ValidationType.VALIDATE)) {
        continue;
      }
      result = store.validate(authCredential);

      if (result.getStatus() != Status.NOT_VALIDATED) {
        validated = true;
      }

      if (result.getStatus() == Status.VALID) {
        break;
      }
    }

    // Never validated
    if (!validated) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    // Ever validated and INVALID
    if (result.getStatus() == Status.INVALID) {
      return result;
    }

    // Groups
    Set<String> groups = new LinkedHashSet<>();

    if (CollectionUtils.hasElements(result.getCallerGroups())) {
      groups.addAll(result.getCallerGroups());
    }

    for (var store : identityStores) {
      if (!store.validationTypes().contains(ValidationType.PROVIDE_GROUPS)) {
        continue;
      }
      var callerGroups = store.getCallerGroups(result);

      if (CollectionUtils.hasElements(callerGroups)) {
        groups.addAll(callerGroups);
      }
    }

    var principal = new AuthUserPrincipal((UserPrincipal) result.getCallerPrincipal(), authCredential);
    return new CredentialValidationResult(principal, groups);
  }

  // @formatter:off
  @Override
  public String toString() {
    var desc = new TextBuilder(512);
    desc.append(ObjectUtils.toIdHash(this)).append("[").appendln();
    
    for (var store : identityStores) {
      desc.appendsp(2).append(ObjectUtils.toIdHash(store)).append("(").appendln()
          .appendsp(4).append("storeId: ").append(store.storeId()).appendln()
          .appendsp(4).append("modules: ").append(String.join(", ", store.validationModules())).appendln()
          .appendsp(4).append("validationTypes: ")
          .append(store.validationTypes().stream()
              .map(Enum::name)
              .collect(Collectors.joining(", ")))
          .appendln()
          .appendsp(2).appendln(")");
    }
    desc.append("]").appendln();
    return desc.toString();
  }
  // @formatter:on
}
