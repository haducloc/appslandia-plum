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

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class RemoteIpVerifierTest {

  @Test
  public void test() {
    try {
      var verifier = new RemoteIpVerifier();

      Assertions.assertTrue(verifier.allow("192.168.10.1"));
      Assertions.assertTrue(verifier.allow("192.168.11.1"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_setAllows() {
    try {
      var verifier = new RemoteIpVerifier();
      verifier.setAllows("192\\.168\\.10\\.\\d+");

      Assertions.assertTrue(verifier.allow("192.168.10.1"));
      Assertions.assertFalse(verifier.allow("192.168.11.1"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_setDenies() {
    try {
      var verifier = new RemoteIpVerifier();
      verifier.setDenies("192\\.168\\.10\\.\\d+");

      Assertions.assertFalse(verifier.allow("192.168.10.1"));
      Assertions.assertTrue(verifier.allow("192.168.11.1"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_combine() {
    try {
      var verifier = new RemoteIpVerifier();
      verifier.setAllows("192\\.168\\.10\\.\\d+");
      verifier.setDenies("192\\.168\\.11\\.\\d+");

      Assertions.assertTrue(verifier.allow("192.168.10.1"));
      Assertions.assertTrue(verifier.allow("192.168.10.10"));
      Assertions.assertFalse(verifier.allow("192.168.11.1"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
