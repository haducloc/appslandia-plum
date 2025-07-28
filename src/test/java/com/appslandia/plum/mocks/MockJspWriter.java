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

package com.appslandia.plum.mocks;

import java.io.IOException;
import java.io.PrintWriter;

import com.appslandia.common.base.StringOutput;

import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
public class MockJspWriter extends JspWriter {

  final StringOutput buffer;
  final PrintWriter out;

  public MockJspWriter() {
    super(0, false);
    this.buffer = new StringOutput();
    this.out = new PrintWriter(this.buffer);
  }

  public String getContent() {
    return this.buffer.toString();
  }

  @Override
  public void newLine() throws IOException {
    this.out.println();
  }

  @Override
  public void print(boolean b) throws IOException {
    this.out.print(b);
  }

  @Override
  public void print(char c) throws IOException {
    this.out.print(c);
  }

  @Override
  public void print(int i) throws IOException {
    this.out.print(i);
  }

  @Override
  public void print(long l) throws IOException {
    this.out.print(l);
  }

  @Override
  public void print(float f) throws IOException {
    this.out.print(f);
  }

  @Override
  public void print(double d) throws IOException {
    this.out.print(d);
  }

  @Override
  public void print(char[] s) throws IOException {
    this.out.print(s);
  }

  @Override
  public void print(String s) throws IOException {
    this.out.print(s);
  }

  @Override
  public void print(Object obj) throws IOException {
    this.out.print(obj);
  }

  @Override
  public void println() throws IOException {
    this.out.println();
  }

  @Override
  public void println(boolean x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(char x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(int x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(long x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(float x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(double x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(char[] x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(String x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void println(Object x) throws IOException {
    this.out.println(x);
  }

  @Override
  public void clear() throws IOException {
  }

  @Override
  public void clearBuffer() throws IOException {
  }

  @Override
  public void flush() throws IOException {
    this.out.flush();
  }

  @Override
  public void close() throws IOException {
    this.out.close();
  }

  @Override
  public int getRemaining() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    this.out.write(cbuf, off, len);
  }

  @Override
  public String toString() {
    return this.buffer.toString();
  }
}
