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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlSymbolProvider extends InitializingObject {

  private Map<String, HtmlSymbol> symbolMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    var symbolMap = this.symbolMap;
    initDefaults(symbolMap);

    this.symbolMap = Collections.unmodifiableMap(symbolMap);
  }

  public HtmlSymbol getHtmlSymbol(String name) {
    initialize();
    var impl = symbolMap.get(name);
    return Arguments.notNull(impl, "No HtmlSymbol is registered for name '{}'.", name);
  }

  public void registerHtmlSymbol(String name, String code) {
    assertNotInitialized();
    var symbol = new HtmlSymbol(name, code);
    symbolMap.put(name, symbol);
  }

  protected void initDefaults(Map<String, HtmlSymbol> symbolMap) {
    register(symbolMap, "check", "&#10003;"); // ✓ (Check mark)
    register(symbolMap, "check-heavy", "&#10004;"); // ✔ (Heavy check mark)
    register(symbolMap, "xmark", "&#10007;"); // ✗ (Ballot X)
    register(symbolMap, "xmark-heavy", "&#10008;"); // ✘ (Heavy ballot X)
  }

  protected void register(Map<String, HtmlSymbol> symbolMap, String name, String code) {
    symbolMap.putIfAbsent(name, new HtmlSymbol(name, code));
  }
}
