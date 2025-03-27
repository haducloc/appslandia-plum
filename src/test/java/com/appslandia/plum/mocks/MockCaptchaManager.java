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

package com.appslandia.plum.mocks;

import java.util.UUID;

import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.base.SessionCaptchaManager;

/**
 *
 * @author Loc Ha
 *
 */
public class MockCaptchaManager extends SessionCaptchaManager {

  public static final String MOCK_CAPTCHA_ID = UUID.randomUUID().toString();
  public static final String MOCK_CAPTCHA_WORDS = "mockCaptchaWords";

  final TextGenerator wordsGenerator = new TextGenerator() {

    @Override
    public String generate() {
      return MOCK_CAPTCHA_WORDS;
    }

    @Override
    public boolean verify(String value) {
      Arguments.notNull(value);
      return value.equals(MOCK_CAPTCHA_WORDS);
    }
  };

  @Override
  protected String generateCaptchaId() {
    return MOCK_CAPTCHA_ID;
  }

  @Override
  protected TextGenerator getWordsGenerator() {
    return this.wordsGenerator;
  }
}
