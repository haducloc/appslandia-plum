// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
