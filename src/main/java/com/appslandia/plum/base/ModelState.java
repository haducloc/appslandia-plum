// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ModelState implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String REQUEST_ATTRIBUTE_ID = "modelState";
  public static final String MODEL_FIELD = "__model";

  private String form;
  final Map<String, List<Message>> errors = new HashMap<>();

  public void setForm(String form) {
    this.form = form;
  }

  public String getForm() {
    return this.form;
  }

  public void addError(String message) {
    getFieldErrors(MODEL_FIELD).add(new Message(Message.TYPE_ERROR, message));
  }

  public void addError(String message, boolean escXml) {
    getFieldErrors(MODEL_FIELD).add(new Message(Message.TYPE_ERROR, message, escXml));
  }

  public void addError(String fieldName, String message) {
    getFieldErrors(fieldName).add(new Message(Message.TYPE_ERROR, message));
  }

  public void addError(String fieldName, String message, boolean escXml) {
    getFieldErrors(fieldName).add(new Message(Message.TYPE_ERROR, message, escXml));
  }

  public List<Message> getFieldErrors(String fieldName) {
    List<Message> errors = this.errors.get(fieldName);
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
    Iterator<String> it = this.errors.keySet().iterator();
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

  public Map<String, String> toErrorMapLocal(Object model) throws Exception {
    final Map<String, String> map = toErrorMap();
    ModelStateUtils.traverse(model, "", "", (path, pathLocal) -> {

      if (!path.equals(pathLocal) && map.containsKey(path)) {
        map.put(pathLocal, map.remove(path));
      }
    });
    return map;
  }

  public Integer getErrorChildIndex(int childCount, Function<Integer, String> childPath) {
    var errIdx = IntStream.range(0, childCount).filter(idx -> this.errors.containsKey(childPath.apply(idx)))
        .findFirst();
    return errIdx.isPresent() ? errIdx.getAsInt() : null;
  }
}
