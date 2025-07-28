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
    var symbolMap = this.symbolMap;
    initDefaults(symbolMap);

    this.symbolMap = Collections.unmodifiableMap(symbolMap);
  }

  public HtmlSymbol getHtmlSymbol(String name) {
    this.initialize();
    var impl = this.symbolMap.get(name);
    return Arguments.notNull(impl, "No HtmlSymbol is registered for name '{}'.", name);
  }

  public void registerHtmlSymbol(String name, String code) {
    this.assertNotInitialized();
    var symbol = new HtmlSymbol(name, code);
    this.symbolMap.put(name, symbol);
  }

  protected void initDefaults(Map<String, HtmlSymbol> symbolMap) {
    // Spaces & Dashes
    register(symbolMap, "nbsp", "&nbsp;"); //   (Non-breaking space)
    register(symbolMap, "ensp", "&ensp;"); // (En space)
    register(symbolMap, "emsp", "&emsp;"); // (Em space)
    register(symbolMap, "thinsp", "&thinsp;"); // (Thin space)
    register(symbolMap, "ndash", "&ndash;"); // – (En dash)
    register(symbolMap, "mdash", "&mdash;"); // — (Em dash)
    register(symbolMap, "horbar", "&horbar;"); // ― (Horizontal bar)

    // Quotes
    register(symbolMap, "lsquo", "&lsquo;"); // ‘ (Left single quotation mark)
    register(symbolMap, "rsquo", "&rsquo;"); // ’ (Right single quotation mark)
    register(symbolMap, "ldquo", "&ldquo;"); // “ (Left double quotation mark)
    register(symbolMap, "rdquo", "&rdquo;"); // ” (Right double quotation mark)

    // Arrows
    register(symbolMap, "larr", "&larr;"); // ← (Leftwards arrow)
    register(symbolMap, "uarr", "&uarr;"); // ↑ (Upwards arrow)
    register(symbolMap, "rarr", "&rarr;"); // → (Rightwards arrow)
    register(symbolMap, "darr", "&darr;"); // ↓ (Downwards arrow)
    register(symbolMap, "udarr", "&#8645;"); // ⇅ (Up-down arrow)
    register(symbolMap, "ldarr", "&lArr;"); // ⇐ (Leftwards double arrow)
    register(symbolMap, "rdarr", "&rArr;"); // ⇒ (Rightwards double arrow)
    register(symbolMap, "harr", "&harr;"); // ↔ (Left-right arrow)
    register(symbolMap, "nearr", "&nearr;"); // ↗ (North-east arrow)
    register(symbolMap, "searr", "&searr;"); // ↘ (South-east arrow)
    register(symbolMap, "nwarr", "&nwarr;"); // ↖ (North-west arrow)
    register(symbolMap, "swarr", "&swarr;"); // ↙ (South-west arrow)

    // Check & Cross Marks
    register(symbolMap, "check", "&#10003;"); // ✓ (Check mark)
    register(symbolMap, "check-heavy", "&#10004;"); // ✔ (Heavy check mark)
    register(symbolMap, "xmark", "&#10007;"); // ✗ (Ballot X)
    register(symbolMap, "xmark-heavy", "&#10008;"); // ✘ (Heavy ballot X)

    // Others
    register(symbolMap, "hellip", "&hellip;"); // … (Horizontal ellipsis)
    register(symbolMap, "verbar", "&verbar;"); // | (Vertical bar)
  }

  protected void register(Map<String, HtmlSymbol> symbolMap, String name, String code) {
    symbolMap.putIfAbsent(name, new HtmlSymbol(name, code));
  }
}
