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
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.RandomUtils;
import com.appslandia.common.utils.SecureRand;

/**
 *
 * @author Loc Ha
 *
 */
public class WordsRendererImpl extends InitializeObject implements WordsRenderer {

  final List<Color> textColors = new ArrayList<>();
  final List<Font> textFonts = new ArrayList<>();

  @Override
  protected void init() throws Exception {
    if (this.textColors.isEmpty()) {
      textColor(Color.LIGHT_GRAY.darker()).textColor(Color.GRAY).textColor(Color.DARK_GRAY);
    }

    if (this.textFonts.isEmpty()) {
      textFont(Font.SERIF, Font.PLAIN, 36).textFont(Font.SANS_SERIF, Font.PLAIN, 36).textFont(Font.DIALOG, Font.PLAIN,
          36);
    }
  }

  @Override
  public void render(BufferedImage img, int width, int height, String words) {
    this.initialize();
    var g = img.createGraphics();

    // hints
    var hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
    g.setRenderingHints(hints);

    // Baseline
    var xBaseline = (int) (0.05f * width);
    var yBaseline = (int) (0.80f * height);

    var drawChars = new char[1];
    var srcChars = words.toCharArray();
    Random rand = SecureRand.getInstance();

    for (var i = 0; i < srcChars.length; i++) {
      drawChars[0] = srcChars[i];

      g.setColor(this.textColors.get(rand.nextInt(this.textColors.size())));
      var rdFont = this.textFonts.get(rand.nextInt(this.textFonts.size()));
      g.setFont(rdFont);

      if (i > 0) {
        xBaseline += RandomUtils.nextInt(4, 12, rand);
      }
      var y = yBaseline + RandomUtils.nextInt(-4, 4, rand);
      var rotationAngle = rand.nextBoolean() ? rand.nextDouble() * Math.PI / 12 : -rand.nextDouble() * Math.PI / 12;

      g.rotate(rotationAngle, xBaseline, y);

      g.drawChars(drawChars, 0, drawChars.length, xBaseline, y);
      g.rotate(-rotationAngle, xBaseline, y);

      var gv = rdFont.createGlyphVector(g.getFontRenderContext(), drawChars);
      xBaseline += (int) gv.getVisualBounds().getWidth();
    }
    g.dispose();
  }

  public WordsRendererImpl textColor(Color color) {
    this.assertNotInitialized();
    this.textColors.add(color);
    return this;
  }

  public WordsRendererImpl textFont(Font font) {
    this.assertNotInitialized();
    this.textFonts.add(font);
    return this;
  }

  public WordsRendererImpl textFont(String name, int style, int size) {
    this.assertNotInitialized();
    this.textFonts.add(new Font(name, style, size));
    return this;
  }
}
