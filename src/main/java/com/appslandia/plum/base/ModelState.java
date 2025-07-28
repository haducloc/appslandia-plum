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

import com.appslandia.common.models.LocalIdSupport;

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
    return this.form;
  }

  public void addError(String message) {
    getFieldErrors(FIELD_MODEL).add(new Message(Message.TYPE_ERROR, message));
  }

  public void addError(String message, boolean escXml) {
    getFieldErrors(FIELD_MODEL).add(new Message(Message.TYPE_ERROR, message, escXml));
  }

  public void addError(String fieldName, String message) {
    getFieldErrors(fieldName).add(new Message(Message.TYPE_ERROR, message));
  }

  public void addError(String fieldName, String message, boolean escXml) {
    getFieldErrors(fieldName).add(new Message(Message.TYPE_ERROR, message, escXml));
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
    this.errors.clear();
  }

  public void remove(Function<String, Boolean> matcher) {
    var it = this.errors.keySet().iterator();
    while (it.hasNext()) {
      if (matcher.apply(it.next())) {
        it.remove();
      }
    }
  }

  public void remove(String... fieldNames) {
    Arrays.stream(fieldNames).forEach(fieldName -> this.errors.remove(fieldName));
  }

  public boolean isValid() {
    return this.errors.isEmpty();
  }

  public boolean isValid(String fieldName) {
    return !this.errors.containsKey(fieldName);
  }

  public boolean isError(String prefixPath) {
    return this.errors.keySet().stream().anyMatch(k -> k.startsWith(prefixPath));
  }

  public boolean areValid(String... fieldNames) {
    return !Arrays.stream(fieldNames).anyMatch(fieldName -> this.errors.containsKey(fieldName));
  }

  public Map<String, List<Message>> getErrors() {
    return this.errors;
  }

  public Map<String, String> toErrorMap() {
    return this.errors.entrySet().stream().collect(
        Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0).getText(), (v1, v2) -> v1, TreeMap::new));
  }

  public Map<String, String> toErrorMapLID(Object model) throws Exception {
    final var map = toErrorMap();
    ModelStateUtils.traverse(model, "", "", (path, pathLocal) -> {

      if (!path.equals(pathLocal) && map.containsKey(path)) {
        map.put(pathLocal, map.remove(path));
      }
    });
    return map;
  }

  public Integer getErrorIndex(List<?> subModelList, String listFieldName) {
    var idx = IntStream.range(0, subModelList.size())
        .filter(i -> this.errors.containsKey(listFieldName + "[" + i + "].")).findFirst();
    return idx.isPresent() ? idx.getAsInt() : null;
  }

  public Integer getErrorLID(List<LocalIdSupport> subModelList, String listFieldName) {
    var idx = getErrorIndex(subModelList, listFieldName);
    var record = subModelList.get(idx);
    return (idx != null) ? record.getLocalId() : null;
  }
}
