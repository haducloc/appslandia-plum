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

package com.appslandia.plum.jsp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class PipeTest {

  @Test
  public void test_upper() {
    try {
      Object v = Pipe.transform("btc", "upper");
      Assertions.assertEquals("BTC", v);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_lower() {
    try {
      Object v = Pipe.transform("JAVAEE-7", "lower");
      Assertions.assertEquals("javaee-7", v);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_trunc() {
    try {
      Object v = Pipe.transform("123456789", "trunc:4");
      Assertions.assertEquals("1234&ellipsis;", v);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_fmtGroup() {
    try {
      Object v = Pipe.transform("4028888888", "fmtGroup:{3}-{3}-{4}");
      Assertions.assertEquals("402-888-8888", v);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_maskStart() {
    try {
      Object v = Pipe.transform("0123456789", "maskStart:5");
      Assertions.assertEquals("*****56789", v);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_maskEnd() {
    try {
      Object v = Pipe.transform("0123456789", "maskEnd:5");
      Assertions.assertEquals("01234*****", v);
    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
