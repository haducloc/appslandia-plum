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

package com.appslandia.plum.defaults;

import com.appslandia.common.cdi.CDIFactory;
import com.appslandia.plum.captcha.CaptchaProducer;
import com.appslandia.plum.captcha.GradientBgProducer;
import com.appslandia.plum.captcha.GridRenderer;
import com.appslandia.plum.captcha.WordsRendererImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultCaptchaProducerFactory implements CDIFactory<CaptchaProducer> {

  @Produces
  @ApplicationScoped
  @Override
  public CaptchaProducer produce() {
    var impl = new CaptchaProducer();

    impl.setBackgroundProducer(new GradientBgProducer());
    impl.addImageRenderer(new GridRenderer());
    impl.setWordsRenderer(new WordsRendererImpl());

    return impl;
  }

  @Override
  public void dispose(@Disposes CaptchaProducer impl) {
  }
}
