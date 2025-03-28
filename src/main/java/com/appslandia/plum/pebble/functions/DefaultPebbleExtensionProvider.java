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

package com.appslandia.plum.pebble.functions;

import java.util.HashMap;
import java.util.Map;

import com.appslandia.plum.pebble.PebbleExtensionProvider;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.extension.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class DefaultPebbleExtensionProvider extends PebbleExtensionProvider {

  @Override
  public Map<String, Function> getFunctions() {
    Map<String, Function> impls = new HashMap<>();

    impls.put("label", new LabelFunction());
    impls.put("input", new InputFunction());
    impls.put("checkbox", new CheckboxFunction());
    impls.put("radio", new RadioFunction());
    impls.put("textarea", new TextAreaFunction());

    impls.put("select", new SelectFunction());
    impls.put("options", new OptionsFunction());
    impls.put("datalist", new DatalistFunction());

    impls.put("hiddenChk", new HiddenCheckboxFunction());
    impls.put("hiddenRdo", new HiddenRadioFunction());
    impls.put("hiddenSel", new HiddenSelectFunction());

    impls.put("fieldClass", new FieldClassFunction());
    impls.put("labelClass", new LabelClassFunction());
    impls.put("ifClass", new IfClassFunction());

    impls.put("error", new FieldErrorFunction());
    impls.put("errors", new FormErrorsFunction());
    impls.put("messages", new MessagesFunction());

    impls.put("actionUrl", new ActionUrlFunction());
    impls.put("userDName", new UserDNameFunction());

    impls.put("envName", new EnvNameFunction());
    impls.put("nowMs", new NowMsFunction());

    impls.put("const", new ConstFunction());
    impls.put("fmtGroup", new FmtGroupFunction());

    impls.put("encParam", new EncodeParamFunction());
    impls.put("encPath", new EncodePathFunction());

    return impls;
  }

  @Override
  public Map<String, Test> getTests() {
    Map<String, Test> impls = new HashMap<>();

    impls.put("env", new IsEnvTest());
    return impls;
  }
}
