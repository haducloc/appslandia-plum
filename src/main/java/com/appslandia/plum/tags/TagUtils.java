// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.tags;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class TagUtils {

  public static final String CSS_D_NONE = "d-none";

  public static final String CSS_FIELD_ERROR = "l-field-error";
  public static final String CSS_LABEL_ERROR = "l-label-error";
  public static final String CSS_LABEL_REQUIRED = "l-label-required";
  public static final String CSS_ERROR_MSG = "l-error-msg";

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
    var selChoices = SplitUtils.splitByComma(handlingValue);
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
    return toTagId(pathExpression, null);
  }

  public static String toTagId(String pathExpression, String prefix) {
    var len = pathExpression.length();

    var buf = (prefix == null) ? new char[len] : new char[len + prefix.length()];

    var offset = 0;

    if (prefix != null) {
      prefix.getChars(0, prefix.length(), buf, 0);
      offset = prefix.length();
    }

    for (var i = 0; i < len; i++) {
      var c = pathExpression.charAt(i);
      buf[offset + i] = (c == '.' || c == '[' || c == ']') ? '_' : c;
    }

    return new String(buf);
  }

  private static final Pattern NON_ID_PART_PATTERN = Pattern.compile("[^a-z\\d_]", Pattern.CASE_INSENSITIVE);
  private static final Pattern UNDERSCORES_PATTERN = Pattern.compile("_{2,}");

  public static String toIdPart(Object value) {
    if (value == null) {
      return StringUtils.NULL_STRING;
    }
    var part = NormalizeUtils.normalize(value.toString(), "_", NON_ID_PART_PATTERN, UNDERSCORES_PATTERN);
    part = StringUtils.trimChar(part, '_');
    return Asserts.notNull(part);
  }

  public static final String ID_PREFIX_FORM_LABEL = "lbl_";
  public static final String ID_PREFIX_FIELD_ERROR = "err_";

  public static String toFormLabelId(String pathExpression) {
    return toTagId(pathExpression, ID_PREFIX_FORM_LABEL);
  }

  public static String toFieldErrorId(String pathExpression) {
    return toTagId(pathExpression, ID_PREFIX_FIELD_ERROR);
  }
}
