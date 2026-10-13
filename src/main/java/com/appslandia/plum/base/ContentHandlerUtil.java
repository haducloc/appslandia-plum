// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.common.base.MemoryStream.BlockIterator;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.EtagUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ContentHandlerUtil {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected Md5DigestPool md5DigestPool;

  @Inject
  protected CompressHandlerProvider compressHandlerProvider;

  public void finishEtag(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext,
      MemoryResponseWrapper wrapper) throws Exception {

    var statusWrapper = ServletUtils.unwrap(response, HttpResponseWrapper.class);
    Asserts.notNull(statusWrapper);

    // 3XX
    if (300 <= statusWrapper.getTrackedStatus() && statusWrapper.getTrackedStatus() < 400) {
      return;
    }

    wrapper.finishResponse();
    var content = wrapper.getContent();

    // ETAG value
    var computedEtag = md5DigestPool.execute((md) -> {
      content.iterate(new BlockIterator() {

        @Override
        public void onNext(byte[] buf, int len) {
          md.update(buf, 0, len);
        }
      });
      return EtagUtils.toStrongEtag(md.digest());
    });

    // If Modified?
    if (!ServletUtils.checkNotModified(request, response, computedEtag)) {

      var compressHandler = (requestContext.getActionDesc().getEnableCompress() != null)
          ? compressHandlerProvider.getHandler(request)
          : null;

      if (compressHandler != null && content.length() >= appConfig.getInt(AppConfig.CONFIG_COMPRESS_THRESHOLD)) {
        compressHandler.compressResponse(request, response, content);

      } else {
        response.setContentLengthLong(content.length());

        if (!HttpMethod.HEAD.equals(request.getMethod())) {
          content.writeTo(response.getOutputStream());
        }
      }
    }
  }
}
