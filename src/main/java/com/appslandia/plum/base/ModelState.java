// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.appslandia.plum.base.Message.MsgType;
import com.appslandia.plum.models.LocalIdSupport;

/**
 *
 * @author Loc Ha
 *
 */
public class ModelState implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String REQUEST_ATTRIBUTE_ID = "__model_state";
  public static final String FIELD_MODEL = "__model";

  final String form;
  final Map<String, List<Message>> errors = new HashMap<>();

  public ModelState(String form) {
    this.form = form;
  }

  public String getForm() {
    return form;
  }

  public void addError(String message) {
    getFieldErrors(FIELD_MODEL).add(new Message(MsgType.ERROR, message));
  }

  public void addError(String message, boolean escXml) {
    getFieldErrors(FIELD_MODEL).add(new Message(MsgType.ERROR, message, escXml));
  }

  public void addError(String fieldName, String message) {
    getFieldErrors(fieldName).add(new Message(MsgType.ERROR, message));
  }

  public void addError(String fieldName, String message, boolean escXml) {
    getFieldErrors(fieldName).add(new Message(MsgType.ERROR, message, escXml));
  }

  public List<Message> getFieldErrors(String fieldName) {
    var errors = this.errors.get(fieldName);
    if (errors == null) {
      errors = new ArrayList<>(5);
      this.errors.put(fieldName, errors);
    }
    return errors;
  }

  public void clearErrors() {
    errors.clear();
  }

  public void remove(Function<String, Boolean> matcher) {
    var it = errors.keySet().iterator();
    while (it.hasNext()) {
      if (matcher.apply(it.next())) {
        it.remove();
      }
    }
  }

  public void remove(String... fieldNames) {
    Arrays.stream(fieldNames).forEach(fieldName -> errors.remove(fieldName));
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  public boolean isValid(String fieldName) {
    return !errors.containsKey(fieldName);
  }

  public boolean isError(String prefixPath) {
    return errors.keySet().stream().anyMatch(k -> k.startsWith(prefixPath));
  }

  public boolean areValid(String... fieldNames) {
    return !Arrays.stream(fieldNames).anyMatch(fieldName -> errors.containsKey(fieldName));
  }

  public Map<String, List<Message>> getErrors() {
    return errors;
  }

  public Map<String, String> toErrorMap() {
    return errors.entrySet().stream().collect(
        Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0).getText(), (v1, v2) -> v1, TreeMap::new));
  }

  public Map<String, String> toErrorMapLID(Object model, Function<String, Boolean> isSubModel) throws Exception {
    final var map = toErrorMap();
    ModelStateUtils.traverse(model, "", "", (path, pathLocal) -> {

      if (!path.equals(pathLocal) && map.containsKey(path)) {
        map.put(pathLocal, map.remove(path));
      }
    }, isSubModel);
    return map;
  }

  public Integer getErrorIndex(List<?> subModelList, String listFieldName) {
    var idx = IntStream.range(0, subModelList.size()).filter(i -> errors.containsKey(listFieldName + "[" + i + "]."))
        .findFirst();
    return idx.isPresent() ? idx.getAsInt() : null;
  }

  public Integer getErrorLID(List<LocalIdSupport> subModelList, String listFieldName) {
    var idx = getErrorIndex(subModelList, listFieldName);
    var record = subModelList.get(idx);
    return (idx != null) ? record.getLocalId() : null;
  }
}
