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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CspValueBuilderTest {

    @Test
    public void test() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.self().data();
	Assertions.assertEquals("'self' data:", csp.toString());
    }

    @Test
    public void test_any() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.any();
	Assertions.assertEquals("*", csp.toString());
    }

    @Test
    public void test_self() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.self();
	Assertions.assertEquals("'self'", csp.toString());
    }

    @Test
    public void test_none() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.none();
	Assertions.assertEquals("'none'", csp.toString());
    }

    @Test
    public void test_unsafeInline() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.unsafeInline();
	Assertions.assertEquals("'unsafe-inline'", csp.toString());
    }

    @Test
    public void test_unsafeEval() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.unsafeEval();
	Assertions.assertEquals("'unsafe-eval'", csp.toString());
    }

    @Test
    public void test_unsafeHashedAttributes() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.unsafeHashedAttributes();
	Assertions.assertEquals("'unsafe-hashed-attributes'", csp.toString());
    }

    @Test
    public void test_https() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.https();
	Assertions.assertEquals("https:", csp.toString());
    }

    @Test
    public void test_data() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.data();
	Assertions.assertEquals("data:", csp.toString());
    }

    @Test
    public void test_mediastream() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.mediastream();
	Assertions.assertEquals("mediastream:", csp.toString());
    }

    @Test
    public void test_blob() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.blob();
	Assertions.assertEquals("blob:", csp.toString());
    }

    @Test
    public void test_filesystem() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.filesystem();
	Assertions.assertEquals("filesystem:", csp.toString());
    }

    @Test
    public void test_nonce() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.nonce("testNonce");
	Assertions.assertEquals("'nonce-testNonce'", csp.toString());
    }

    @Test
    public void test_sha256() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.sha256("testSha256");
	Assertions.assertEquals("'sha256-testSha256'", csp.toString());
    }

    @Test
    public void test_sha386() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.sha386("testSha386");
	Assertions.assertEquals("'sha386-testSha386'", csp.toString());
    }

    @Test
    public void test_sha512() {
	CspValueBuilder csp = new CspValueBuilder();
	csp.sha512("testSha512");
	Assertions.assertEquals("'sha512-testSha512'", csp.toString());
    }
}
