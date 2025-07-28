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

package com.appslandia.plum.tags;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.SplittingBehavior;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class TagUtils {

  public static final String CSS_NOOP = "l-noop";
  public static final String CSS_D_NONE = "l-d-none";

  public static final String CSS_CHOICE_ERROR = "l-choice-error";
  public static final String CSS_FIELD_ERROR = "l-field-error";
  public static final String CSS_LABEL_ERROR = "l-label-error";
  public static final String CSS_LABEL_REQUIRED = "l-label-required";

  public static boolean isDynamicParameter(String attribute) {
    return attribute.startsWith("__");
  }

  public static String getDynParamName(String attribute) {
    return attribute.substring(2);
  }

  public static boolean isCheckboxChecked(String choiceValue, String handlingValue) {
    if (Objects.equals(choiceValue, handlingValue)) {
      return true;
    }
    if (handlingValue == null) {
      return false;
    }
    var selChoices = SplitUtils.split(handlingValue, ',', SplittingBehavior.SKIP_NULL);
    return Arrays.stream(selChoices).anyMatch(selChoice -> Objects.equals(choiceValue, selChoice));
  }

  public static boolean isRadioChecked(String choiceValue, String handlingValue) {
    return Objects.equals(choiceValue, handlingValue);
  }

  // @formatter:off
  private static final Set<String> VALID_INPUT_TYPES = Set.of(
      "text",
      "password",
      "email",
      "number",
      "search",
      "tel",
      "url",
      // "hidden",
      "date",
      "datetime-local",
      "month",
      "time",
      "week",
      "color",
      "range"
  );
  // @formatter:on

  public static boolean isValidInputType(String inputType) {
    return VALID_INPUT_TYPES.contains(inputType);
  }

  // pathExpression: userName, users[0].userName, profile.userName

  public static String toTagId(String pathExpression) {
    var len = pathExpression.length();
    var i = -1;
    char buf[] = new char[pathExpression.length()];

    while (i < len - 1) {
      i++;
      var c = pathExpression.charAt(i);
      buf[i] = ((c == '.') || (c == '[') || (c == ']')) ? ('_') : c;
    }
    return new String(buf);
  }

  private static final Pattern NON_ID_PART_PATTERN = Pattern.compile("[^a-z\\d_]", Pattern.CASE_INSENSITIVE);
  private static final Pattern UNDERSCORE2_PATTERN = Pattern.compile("_{2,}");

  public static String toIdPart(Object value) {
    if (value == null) {
      return StringUtils.NULL_STRING;
    }
    var part = NormalizeUtils.normalize(value.toString(), "_", NON_ID_PART_PATTERN, UNDERSCORE2_PATTERN);
    part = StringUtils.trimChar(part, '_');
    return Asserts.notNull(part);
  }
}
