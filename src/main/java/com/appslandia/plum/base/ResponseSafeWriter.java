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

package com.appslandia.plum.base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ResponseSafeWriter extends ResponseWrapper {

	private PrintWriter outWriter;
	private Boolean usedWriter;

	public ResponseSafeWriter(HttpServletResponse response) {
		super(response);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (Boolean.TRUE.equals(this.usedWriter)) {
			return super.getWriter();
		}
		if (this.usedWriter == null) {
			try {
				PrintWriter writer = super.getWriter();
				this.usedWriter = Boolean.TRUE;
				return writer;

			} catch (IllegalStateException ex) {
				this.usedWriter = Boolean.FALSE;
			}
		}
		if (this.outWriter == null) {
			this.outWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(super.getOutputStream(), this.getCharacterEncoding())));
		}
		return this.outWriter;
	}

	@Override
	public void finishWrapper() throws IOException {
	}

	@Override
	public void flushBuffer() throws IOException {
		if (this.outWriter != null) {
			this.outWriter.flush();
		} else {
			super.flushBuffer();
		}
	}

	@Override
	public void resetBuffer() {
		super.resetBuffer();

		this.outWriter = null;
		this.usedWriter = null;
	}

	@Override
	public void reset() {
		super.reset();

		this.outWriter = null;
		this.usedWriter = null;
	}
}
