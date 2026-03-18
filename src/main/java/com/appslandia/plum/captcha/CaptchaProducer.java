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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.appslandia.common.base.InitializingObject;

/**
 *
 * @author Loc Ha
 *
 */
public class CaptchaProducer extends InitializingObject {

  private WordsRenderer wordsRenderer;
  private BackgroundProducer backgroundProducer;
  private List<ImageRenderer> imageRenderers = new ArrayList<>();

  private int width = 200;
  private int height = 50;
  private boolean borderRendered = false;
  private Color borderColor;

  @Override
  protected void init() throws Exception {
    if (wordsRenderer == null) {
      wordsRenderer = new WordsRendererImpl();
    }
    if (backgroundProducer == null) {
      backgroundProducer = new TransparentBgProducer();
    }
    if (borderRendered) {
      if (borderColor == null) {
        borderColor = Color.GRAY;
      }
    }
  }

  public BufferedImage produce(String words) {
    initialize();

    // ARGB
    var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    // Words
    wordsRenderer.render(img, width, height, words);

    // Renders
    for (ImageRenderer renderer : imageRenderers) {
      renderer.render(img, width, height);
    }

    // Background
    var bg = backgroundProducer.produce(width, height);
    var g = bg.createGraphics();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    g.drawImage(img, null, null);

    // Border?
    if (borderRendered) {
      g.setColor(borderColor);
      g.drawLine(0, 0, 0, height - 1);
      g.drawLine(0, 0, width - 1, 0);
      g.drawLine(0, height - 1, width, height - 1);
      g.drawLine(width - 1, height - 1, width - 1, 0);
    }
    g.dispose();
    return bg;
  }

  public CaptchaProducer setWordsRenderer(WordsRenderer wordsRender) {
    assertNotInitialized();
    wordsRenderer = wordsRender;
    return this;
  }

  public CaptchaProducer setBackgroundProducer(BackgroundProducer backgroundProducer) {
    assertNotInitialized();
    this.backgroundProducer = backgroundProducer;
    return this;
  }

  public CaptchaProducer addImageRenderer(ImageRenderer noiseRenderer) {
    assertNotInitialized();
    imageRenderers.add(noiseRenderer);
    return this;
  }

  public CaptchaProducer setWidth(int width) {
    assertNotInitialized();
    this.width = width;
    return this;
  }

  public CaptchaProducer setHeight(int height) {
    assertNotInitialized();
    this.height = height;
    return this;
  }

  public CaptchaProducer setBorderRendered(boolean borderRendered) {
    assertNotInitialized();
    this.borderRendered = borderRendered;
    return this;
  }

  public CaptchaProducer setBorderColor(Color borderColor) {
    assertNotInitialized();
    this.borderColor = borderColor;
    return this;
  }
}
