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

package com.appslandia.plum.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.RandomUtils;
import com.appslandia.common.utils.SecureRand;

/**
 *
 * @author Loc Ha
 *
 */
public class GridRenderer extends InitializingObject implements ImageRenderer {

  final List<Color> hColors = new ArrayList<>();
  final List<Color> vColors = new ArrayList<>();

  @Override
  protected void init() throws Exception {
    if (hColors.isEmpty()) {
      hColor(Color.LIGHT_GRAY).hColor(Color.GRAY).hColor(Color.DARK_GRAY);
    }

    if (vColors.isEmpty()) {
      vColor(Color.LIGHT_GRAY).vColor(Color.GRAY).vColor(Color.DARK_GRAY);
    }
  }

  @Override
  public void render(BufferedImage img, int width, int height) {
    initialize();
    var g = img.createGraphics();

    Random rand = SecureRand.getInstance();

    // Horizontal Stripes
    for (var i = 8; i < height; i = i + RandomUtils.nextInt(6, 10, rand)) {
      g.setColor(hColors.get(rand.nextInt(hColors.size())));
      g.drawLine(0, i, width, i);
    }

    // Vertical Stripes
    for (var i = 8; i < width; i = i + RandomUtils.nextInt(6, 10, rand)) {
      g.setColor(vColors.get(rand.nextInt(vColors.size())));
      g.drawLine(i, 0, i, height);
    }
    g.dispose();
  }

  public GridRenderer hColor(Color color) {
    assertNotInitialized();
    hColors.add(color);
    return this;
  }

  public GridRenderer vColor(Color color) {
    assertNotInitialized();
    vColors.add(color);
    return this;
  }
}
