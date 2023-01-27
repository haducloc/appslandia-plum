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

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import com.appslandia.common.base.Params;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.BadRequestException;
import com.appslandia.plum.base.CacheControl;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.HttpGetPost;
import com.appslandia.plum.base.RequestAccessor;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.Result;
import com.appslandia.plum.base.SimpleCaptchaManager;
import com.appslandia.plum.captcha.CaptchaProducer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
@Controller("captcha")
@CacheControl(nocache = true)
public class DefaultCaptchaController {

    @Inject
    protected SimpleCaptchaManager captchaManager;

    @Inject
    protected CaptchaProducer captchaProducer;

    @Inject
    protected ActionParser actionParser;

    @HttpGetPost
    public Result index(RequestAccessor request, HttpServletResponse response) throws Exception {
	this.captchaManager.initCaptcha(request);

	String captchaId = (String) request.getAttribute(SimpleCaptchaManager.REQUEST_ATTRIBUTE_CAPTCHA_DATA);
	Map<String, Object> params = Params.of(SimpleCaptchaManager.PARAM_CAPTCHA_ID, captchaId);

	String wordsUrl = this.actionParser.toActionUrl(request, "captcha", "words", params, false);
	return new Result().setData(captchaId).setLink(response.encodeURL(wordsUrl));
    }

    @HttpGet
    public ActionResult words(RequestAccessor request) throws Exception {
	String captchaWords = this.captchaManager.getCaptchaWords(request);
	if (captchaWords == null) {
	    throw new BadRequestException(request.res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
	}
	final BufferedImage img = this.captchaProducer.produce(captchaWords);

	return new ActionResult() {

	    @Override
	    public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) throws Exception {
		response.setContentType("image/png");

		try (OutputStream os = response.getOutputStream()) {
		    ImageIO.write(img, "png", os);
		}
	    }
	};
    }
}
