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

package com.appslandia.plum.captcha;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class GradientBgProducer extends InitializeObject implements BackgroundProducer {

	private Color fromColor;
	private Color toColor;

	@Override
	protected void init() throws Exception {
		AssertUtils.assertNotNull(this.fromColor, "fromColor is required.");
		AssertUtils.assertNotNull(this.toColor, "toColor is required.");
	}

	@Override
	public BufferedImage produce(int width, int height) {
		this.initialize();

		// RGB
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();

		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(hints);
		g.setPaint(new GradientPaint(0, 0, this.fromColor, width, height, this.toColor));

		g.fill(new Rectangle2D.Double(0, 0, width, height));
		g.drawImage(img, 0, 0, null);

		g.dispose();
		return img;
	}

	public GradientBgProducer setFromColor(Color fromColor) {
		this.assertNotInitialized();
		this.fromColor = fromColor;
		return this;
	}

	public GradientBgProducer setToColor(Color toColor) {
		this.assertNotInitialized();
		this.toColor = toColor;
		return this;
	}
}
