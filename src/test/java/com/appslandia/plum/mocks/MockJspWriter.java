// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    buffer = new StringOutput();
    out = new PrintWriter(buffer);
  }

  public String getContent() {
    return buffer.toString();
  }

  @Override
  public void newLine() throws IOException {
    out.println();
  }

  @Override
  public void print(boolean b) throws IOException {
    out.print(b);
  }

  @Override
  public void print(char c) throws IOException {
    out.print(c);
  }

  @Override
  public void print(int i) throws IOException {
    out.print(i);
  }

  @Override
  public void print(long l) throws IOException {
    out.print(l);
  }

  @Override
  public void print(float f) throws IOException {
    out.print(f);
  }

  @Override
  public void print(double d) throws IOException {
    out.print(d);
  }

  @Override
  public void print(char[] s) throws IOException {
    out.print(s);
  }

  @Override
  public void print(String s) throws IOException {
    out.print(s);
  }

  @Override
  public void print(Object obj) throws IOException {
    out.print(obj);
  }

  @Override
  public void println() throws IOException {
    out.println();
  }

  @Override
  public void println(boolean x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(char x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(int x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(long x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(float x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(double x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(char[] x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(String x) throws IOException {
    out.println(x);
  }

  @Override
  public void println(Object x) throws IOException {
    out.println(x);
  }

  @Override
  public void clear() throws IOException {
  }

  @Override
  public void clearBuffer() throws IOException {
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

  @Override
  public int getRemaining() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    out.write(cbuf, off, len);
  }

  @Override
  public String toString() {
    return buffer.toString();
  }
}
