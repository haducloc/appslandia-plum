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

package com.appslandia.plum.results;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.BOMOutputStream;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TextFileResult extends DownloadResult {

	protected String contentEncoding;
	protected String content;

	public TextFileResult(String content, String fileName, String contentType) {
		this(content, fileName, contentType, StandardCharsets.UTF_8.name(), false);
	}

	public TextFileResult(String content, String fileName, String contentType, String contentEncoding) {
		this(content, fileName, contentType, contentEncoding, false);
	}

	public TextFileResult(String content, String fileName, String contentType, String contentEncoding, boolean inline) {
		super(fileName, contentType, inline);

		this.content = content;
		this.contentEncoding = contentEncoding;
	}

	@Override
	protected void writeContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (this.contentEncoding != null) {
			response.setCharacterEncoding(this.contentEncoding);
		}
		OutputStreamWriter out = new OutputStreamWriter(new BOMOutputStream(response.getOutputStream(), response.getCharacterEncoding()),
				response.getCharacterEncoding());
		out.write(this.content);

		out.flush();
	}
}
