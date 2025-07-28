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

import java.util.ArrayList;

/**
 *
 * @author Loc Ha
 *
 */
public class Messages extends ArrayList<Message> {
  private static final long serialVersionUID = 1L;

  public static final String REQUEST_ATTRIBUTE_ID = "__messages";

  public Messages() {
  }

  public Messages(int initialCapacity) {
    super(initialCapacity);
  }

  public void addInfo(String text) {
    this.add(new Message(Message.TYPE_INFO, text));
  }

  public void addNotice(String text) {
    this.add(new Message(Message.TYPE_NOTICE, text));
  }

  public void addWarn(String text) {
    this.add(new Message(Message.TYPE_WARN, text));
  }

  public void addError(String text) {
    this.add(new Message(Message.TYPE_ERROR, text));
  }

  public void addFatal(String text) {
    this.add(new Message(Message.TYPE_FATAL, text));
  }

  public void addSuccess(String text) {
    this.add(new Message(Message.TYPE_SUCCESS, text));
  }
}
