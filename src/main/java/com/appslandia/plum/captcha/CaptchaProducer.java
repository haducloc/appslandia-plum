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

import com.appslandia.common.base.InitializeObject;

/**
 *
 * @author Loc Ha
 *
 */
public class CaptchaProducer extends InitializeObject {

  private WordsRenderer wordsRenderer;
  private BackgroundProducer backgroundProducer;
  private List<ImageRenderer> imageRenderers = new ArrayList<>();

  private int width = 200;
  private int height = 50;
  private boolean borderRendered = false;
  private Color borderColor;

  @Override
  protected void init() throws Exception {
    if (this.wordsRenderer == null) {
      this.wordsRenderer = new WordsRendererImpl();
    }
    if (this.backgroundProducer == null) {
      this.backgroundProducer = new TransparentBgProducer();
    }
    if (this.borderRendered) {
      if (this.borderColor == null) {
        this.borderColor = Color.GRAY;
      }
    }
  }

  public BufferedImage produce(String words) {
    this.initialize();

    // ARGB
    var img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

    // Words
    this.wordsRenderer.render(img, this.width, this.height, words);

    // Renders
    for (ImageRenderer renderer : imageRenderers) {
      renderer.render(img, this.width, this.height);
    }

    // Background
    var bg = this.backgroundProducer.produce(this.width, this.height);
    var g = bg.createGraphics();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    g.drawImage(img, null, null);

    // Border?
    if (this.borderRendered) {
      g.setColor(this.borderColor);
      g.drawLine(0, 0, 0, this.height - 1);
      g.drawLine(0, 0, this.width - 1, 0);
      g.drawLine(0, this.height - 1, this.width, this.height - 1);
      g.drawLine(this.width - 1, this.height - 1, this.width - 1, 0);
    }
    g.dispose();
    return bg;
  }

  public CaptchaProducer setWordsRenderer(WordsRenderer wordsRender) {
    this.assertNotInitialized();
    this.wordsRenderer = wordsRender;
    return this;
  }

  public CaptchaProducer setBackgroundProducer(BackgroundProducer backgroundProducer) {
    this.assertNotInitialized();
    this.backgroundProducer = backgroundProducer;
    return this;
  }

  public CaptchaProducer addImageRenderer(ImageRenderer noiseRenderer) {
    this.assertNotInitialized();
    this.imageRenderers.add(noiseRenderer);
    return this;
  }

  public CaptchaProducer setWidth(int width) {
    this.assertNotInitialized();
    this.width = width;
    return this;
  }

  public CaptchaProducer setHeight(int height) {
    this.assertNotInitialized();
    this.height = height;
    return this;
  }

  public CaptchaProducer setBorderRendered(boolean borderRendered) {
    this.assertNotInitialized();
    this.borderRendered = borderRendered;
    return this;
  }

  public CaptchaProducer setBorderColor(Color borderColor) {
    this.assertNotInitialized();
    this.borderColor = borderColor;
    return this;
  }
}
