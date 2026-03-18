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

package com.appslandia.plum.facelet;

import com.appslandia.common.utils.ParseUtils;

import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentConfig;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.MetaRule;
import jakarta.faces.view.facelets.MetaRuleset;
import jakarta.faces.view.facelets.Metadata;
import jakarta.faces.view.facelets.MetadataTarget;
import jakarta.faces.view.facelets.TagAttribute;

/**
 *
 * @author Loc Ha
 *
 */
public class DynamicAttributesHandler extends ComponentHandler {

  public DynamicAttributesHandler(ComponentConfig config) {
    super(config);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected MetaRuleset createMetaRuleset(Class type) {
    var metaRuleset = super.createMetaRuleset(type);
    metaRuleset.addRule(new MetaRule() {

      @Override
      public Metadata applyRule(String name, TagAttribute attr, MetadataTarget meta) {

        return new Metadata() {

          @Override
          public void applyMetadata(FaceletContext ctx, Object instance) {
            var component = (UIComponent) instance;
            if (attr.isLiteral()) {

              if ("rendered".equals(name)) {
                component.getAttributes().put(name, ParseUtils.parseBool(attr.getValue()));
              } else {
                component.getAttributes().put(name, attr.getValue());
              }

            } else {
              if ("rendered".equals(name)) {
                var ve = attr.getValueExpression(ctx, Boolean.class);
                component.getAttributes().put(name, ve.getValue(ctx));
              } else {
                var ve = attr.getValueExpression(ctx, Object.class);
                component.getAttributes().put(name, ve);
              }
            }
          }
        };
      }
    });
    return metaRuleset;
  }
}
