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

package com.appslandia.plum.jsp;

import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.Message;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MessageUtils {

  public static int getMsgType(String type) {
    return Message.parseType(type);
  }

  public static String getMsgClass(int type) {
    switch (type) {
    case Message.TYPE_INFO:
      return "l-msg-info";

    case Message.TYPE_NOTICE:
      return "l-msg-notice";

    case Message.TYPE_WARN:
      return "l-msg-warn";

    case Message.TYPE_ERROR:
      return "l-msg-error";

    case Message.TYPE_FATAL:
      return "l-msg-fatal";
    default:
      throw new IllegalArgumentException(STR.fmt("type '{}' is invalid.", type));
    }
  }
}
