// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.TextBuilder;
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
    var storeIds = new HashSet<String>();

    for (var store : identityStores) {
      var storeId = store.storeId();
      if (storeId != null) {

        if (!storeIds.add(storeId)) {
          throw new IllegalStateException("Duplicate storeId: " + store.storeId());
        }
      }
    }

    // Sort Stores
    Collections.sort(identityStores, Comparator.comparing(IdentityStoreBase::priority));
    identityStores = Collections.unmodifiableList(identityStores);
  }

  @Override
  public CredentialValidationResult validate(Credential credential) {
    return validate(credential, false);
  }

  public CredentialValidationResult validate(Credential credential, boolean testAuthOnly) {
    initialize();

    // AuthCredential
    if (!(credential instanceof AuthCredential authCredential)) {
      throw new IllegalArgumentException(
          "The given credential must be an instance of com.appslandia.plum.base.AuthCredential.");
    }

    // RULES:
    // - Ignore NOT_VALIDATED results.
    // - Stop at the first VALID result.
    // - Include groups from the VALID result only if the validating store supports both VALIDATE and PROVIDE_GROUPS.
    // - Aggregate additional groups from stores configured as PROVIDE_GROUPS only.
    // - If all stores return NOT_VALIDATED, return NOT_VALIDATED.
    // - Otherwise, return the last non-NOT_VALIDATED result.

    CredentialValidationResult validatedResult = null;
    IdentityStoreBase validStore = null;

    for (var store : identityStores) {
      if (!store.isProvideValidate(authCredential.getModule())) {
        continue;
      }
      var result = store.validate(authCredential);

      if (result.getStatus() != Status.NOT_VALIDATED) {
        validatedResult = result;
      }

      if (result.getStatus() == Status.VALID) {
        validStore = store;
        break;
      }
    }

    // Never validated
    if (validatedResult == null) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    // Validated & Invalid
    if (validatedResult.getStatus() == Status.INVALID) {
      return validatedResult;
    }

    // RequestPrincipal
    var principal = (AuthPrincipal) validatedResult.getCallerPrincipal();
    var callerPrincipal = new RequestPrincipal(principal, authCredential.isRememberMe(),
        authCredential.isReauthentication());

    // For Authentication Testing Only
    if (testAuthOnly) {
      return new CredentialValidationResult(principal.getStoreId(), callerPrincipal,
          (String) principal.get(AuthPrincipal.ATTRIBUTE_LDAP_DN), principal.getCallerUniqueId(), null);
    }

    // Collecting caller groups
    var finalGroups = aggregateCallerGroups(authCredential.getModule(), validatedResult);

    // Add validatedResult.getCallerGroups()?
    if (validStore.validationTypes().contains(ValidationType.PROVIDE_GROUPS)) {
      finalGroups.addAll(validatedResult.getCallerGroups());
    }

    return new CredentialValidationResult(principal.getStoreId(), callerPrincipal,
        (String) principal.get(AuthPrincipal.ATTRIBUTE_LDAP_DN), principal.getCallerUniqueId(), finalGroups);
  }

  protected Set<String> aggregateCallerGroups(String module, CredentialValidationResult validResult) {
    Set<String> aggGroups = new HashSet<>();

    for (var store : identityStores) {
      if (!store.isProvideGroupsOnly(module)) {
        continue;
      }

      var groups = store.getCallerGroups(validResult);
      aggGroups.addAll(groups);
    }
    return aggGroups;
  }

  // @formatter:off
  @Override
  public String toString() {
    initialize();

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
