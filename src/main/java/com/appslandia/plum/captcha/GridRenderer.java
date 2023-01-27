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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class GridRenderer extends InitializeObject implements ImageRenderer {

    private Color hColor;
    private Color vColor;

    final Random random = new SecureRandom();

    @Override
    protected void init() throws Exception {
	AssertUtils.assertNotNull(this.hColor, "hColor is required.");
	AssertUtils.assertNotNull(this.vColor, "vColor is required.");
    }

    @Override
    public void render(BufferedImage img, int width, int height) {
	this.initialize();
	Graphics2D g = img.createGraphics();

	int hStripes = height / 7;
	int vStripes = width / 7;
	int hSpace = height / (hStripes + 1);
	int vSpace = width / (vStripes + 1);

	// Horizontal Stripes
	for (int i = hSpace; i < height; i = i + hSpace) {
	    g.setColor(this.hColor);
	    g.drawLine(0, i, width, i);
	}

	// Vertical Stripes
	for (int i = vSpace; i < width; i = i + vSpace) {
	    g.setColor(this.vColor);
	    g.drawLine(i, 0, i, height);
	}
	g.dispose();
    }

    public GridRenderer setHColor(Color hColor) {
	this.assertNotInitialized();
	this.hColor = hColor;
	return this;
    }

    public GridRenderer setVColor(Color vColor) {
	this.assertNotInitialized();
	this.vColor = vColor;
	return this;
    }
}
