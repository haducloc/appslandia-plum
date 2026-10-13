// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    // Core UI feedback
    register(symbolMap, "check", "&#10003;"); // ✓ check mark
    register(symbolMap, "check-heavy", "&#10004;"); // ✔ heavy check mark
    register(symbolMap, "xmark", "&#10007;"); // ✗ ballot X
    register(symbolMap, "xmark-heavy", "&#10008;"); // ✘ heavy ballot X

    register(symbolMap, "success", "&#10004;"); // ✔
    register(symbolMap, "error", "&#10008;"); // ✘
    register(symbolMap, "warn", "&#9888;"); // ⚠
    register(symbolMap, "info", "&#8505;"); // ℹ

    // Text flow
    register(symbolMap, "hellip", "&#8230;"); // … loading / truncation

    // Navigation / pagination
    register(symbolMap, "left", "&#8249;"); // ‹ previous
    register(symbolMap, "right", "&#8250;"); // › next
    register(symbolMap, "first", "&#171;"); // « first page
    register(symbolMap, "last", "&#187;"); // » last page

    // Actions
    register(symbolMap, "plus", "&#43;"); // + add
    register(symbolMap, "minus", "&#8722;"); // − collapse/remove
    register(symbolMap, "close", "&#215;"); // × close dialog
    register(symbolMap, "required", "&#42;"); // * required field
    register(symbolMap, "external", "&#8599;"); // ↗ external link
    register(symbolMap, "refresh", "&#8635;"); // ↻ refresh/reload

    // Toggle / expand
    register(symbolMap, "expand", "&#9654;"); // ▶ expand
    register(symbolMap, "collapse", "&#9660;"); // ▼ collapse

    // Sorting / table headers
    register(symbolMap, "sort-up", "&#9650;"); // ▲ asc
    register(symbolMap, "sort-down", "&#9660;"); // ▼ desc
    register(symbolMap, "sort-none", "&#8597;"); // ↕ unsorted

    // Separators / layout
    register(symbolMap, "bullet", "&#8226;"); // • list bullet
    register(symbolMap, "middot", "&#183;"); // · inline separator
    register(symbolMap, "nbsp", "&#160;"); // non-breaking space

    // Common text punctuation
    register(symbolMap, "ndash", "&#8211;"); // – range
    register(symbolMap, "mdash", "&#8212;"); // — emphasis

    // Common state / metadata
    register(symbolMap, "lock", "&#128274;"); // 🔒 locked/security
    register(symbolMap, "clock", "&#128337;"); // 🕑 time/timestamp
    register(symbolMap, "calendar", "&#128197;"); // 📅 date/calendar

    // Other
    register(symbolMap, "pipe", "&#124;"); // | inline separator (same context)
    register(symbolMap, "divider", "&#9474;"); // │ visual divider (UI separation)
  }

  protected void register(Map<String, HtmlSymbol> symbolMap, String name, String code) {
    symbolMap.putIfAbsent(name, new HtmlSymbol(name, code));
  }
}
