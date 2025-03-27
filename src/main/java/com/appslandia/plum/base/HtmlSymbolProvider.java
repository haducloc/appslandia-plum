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

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlSymbolProvider extends InitializeObject {

  private Map<String, HtmlSymbol> symbolMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    Map<String, HtmlSymbol> symbolMap = this.symbolMap;
    initDefaults(symbolMap);

    this.symbolMap = Collections.unmodifiableMap(symbolMap);
  }

  public HtmlSymbol getHtmlSymbol(String name) {
    this.initialize();
    HtmlSymbol symbol = this.symbolMap.get(name);
    return Arguments.notNull(symbol);
  }

  public void addHtmlSymbol(String name, String code) {
    this.assertNotInitialized();
    HtmlSymbol symbol = new HtmlSymbol(name, code);
    this.symbolMap.put(name, symbol);
  }

  protected void initDefaults(Map<String, HtmlSymbol> symbolMap) {
    // Cross marks
    putIfAbsent(symbolMap, "x-mark", "&cross;"); // ✗
    putIfAbsent(symbolMap, "x-mark-heavy", "&#10008;"); // ✘

    // Check marks
    putIfAbsent(symbolMap, "check-mark", "&check;"); // ✓
    putIfAbsent(symbolMap, "check-mark-heavy", "&#10004;"); // ✔

    // Arrows
    putIfAbsent(symbolMap, "arrow-up", "&#8593;"); // ↑
    putIfAbsent(symbolMap, "arrow-down", "&#8595;"); // ↓
    putIfAbsent(symbolMap, "arrow-up-down", "&#8645;"); // ⇅
    putIfAbsent(symbolMap, "arrow-left", "&#8592;"); // ←
    putIfAbsent(symbolMap, "arrow-right", "&#8594;"); // →
  }

  protected void putIfAbsent(Map<String, HtmlSymbol> symbolMap, String name, String code) {
    symbolMap.putIfAbsent(name, new HtmlSymbol(name, code));
  }
}
