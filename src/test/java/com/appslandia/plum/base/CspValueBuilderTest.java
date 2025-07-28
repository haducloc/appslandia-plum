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
public class CspValueBuilderTest {

  @Test
  public void test_any() {
    var builder = new CspValueBuilder().any();
    Assertions.assertEquals("*", builder.toString());
  }

  @Test
  public void test_none() {
    var builder = new CspValueBuilder().none();
    Assertions.assertEquals("'none'", builder.toString());
  }

  @Test
  public void test_self() {
    var builder = new CspValueBuilder().self();
    Assertions.assertEquals("'self'", builder.toString());
  }

  @Test
  public void test_strictDynamic() {
    var builder = new CspValueBuilder().strictDynamic();
    Assertions.assertEquals("'strict-dynamic'", builder.toString());
  }

  @Test
  public void test_reportSample() {
    var builder = new CspValueBuilder().reportSample();
    Assertions.assertEquals("'report-sample'", builder.toString());
  }

  @Test
  public void test_inlineSpeculationRules() {
    var builder = new CspValueBuilder().inlineSpeculationRules();
    Assertions.assertEquals("'inline-speculation-rules'", builder.toString());
  }

  @Test
  public void test_unsafeInline() {
    var builder = new CspValueBuilder().unsafeInline();
    Assertions.assertEquals("'unsafe-inline'", builder.toString());
  }

  @Test
  public void test_unsafeEval() {
    var builder = new CspValueBuilder().unsafeEval();
    Assertions.assertEquals("'unsafe-eval'", builder.toString());
  }

  @Test
  public void test_unsafeHashes() {
    var builder = new CspValueBuilder().unsafeHashes();
    Assertions.assertEquals("'unsafe-hashes'", builder.toString());
  }

  @Test
  public void test_wasmUnsafeEval() {
    var builder = new CspValueBuilder().wasmUnsafeEval();
    Assertions.assertEquals("'wasm-unsafe-eval'", builder.toString());
  }

  @Test
  public void test_https() {
    var builder = new CspValueBuilder().https();
    Assertions.assertEquals("https:", builder.toString());
  }

  @Test
  public void test_http() {
    var builder = new CspValueBuilder().http();
    Assertions.assertEquals("http:", builder.toString());
  }

  @Test
  public void test_data() {
    var builder = new CspValueBuilder().data();
    Assertions.assertEquals("data:", builder.toString());
  }

  @Test
  public void test_nonce() {
    var nonce = "nonce";
    var builder = new CspValueBuilder().nonce(nonce);
    Assertions.assertEquals("'nonce-" + nonce + "'", builder.toString());
  }

  @Test
  public void test_sha256() {
    var hash = "hash";
    var builder = new CspValueBuilder().sha256(hash);
    Assertions.assertEquals("'sha256-" + hash + "'", builder.toString());
  }

  @Test
  public void test_sha386() {
    var hash = "hash";
    var builder = new CspValueBuilder().sha386(hash);
    Assertions.assertEquals("'sha386-" + hash + "'", builder.toString());
  }

  @Test
  public void test_sha512() {
    var hash = "hash";
    var builder = new CspValueBuilder().sha512(hash);
    Assertions.assertEquals("'sha512-" + hash + "'", builder.toString());
  }

  @Test
  public void test_and() {
    var value = "value";
    var builder = new CspValueBuilder().and(value);
    Assertions.assertEquals(value, builder.toString());
  }
}
