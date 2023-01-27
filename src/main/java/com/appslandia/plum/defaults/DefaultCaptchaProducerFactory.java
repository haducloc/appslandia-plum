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

package com.appslandia.plum.defaults;

import java.awt.Color;

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
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class DefaultCaptchaProducerFactory implements CDIFactory<CaptchaProducer> {

    @Produces
    @ApplicationScoped
    @Override
    public CaptchaProducer produce() {
	CaptchaProducer impl = new CaptchaProducer();
	impl.setBackgroundProducer(new GradientBgProducer().setFromColor(Color.LIGHT_GRAY).setToColor(Color.WHITE));
	impl.addImageRenderer(new GridRenderer().setHColor(Color.GRAY).setVColor(Color.GRAY));
	impl.setWordsRenderer(new WordsRendererImpl().addTextColor(new Color(136, 136, 136)).addTextColor(Color.GRAY).addTextColor(new Color(120, 120, 120)));
	return impl;
    }

    @Override
    public void dispose(@Disposes CaptchaProducer impl) {
    }
}
