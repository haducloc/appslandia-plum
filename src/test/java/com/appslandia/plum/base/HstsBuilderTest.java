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

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HstsBuilderTest {

    @Test
    public void test() {
	HstsBuilder builder = new HstsBuilder();

	builder.maxAge(1000, TimeUnit.SECONDS).includeSubDomains().preload();
	Assertions.assertTrue(builder.toString().contains("max-age=1000; includeSubDomains; preload"));
    }

    @Test
    public void test_maxAge() {
	HstsBuilder builder = new HstsBuilder();

	builder.maxAge(1000, TimeUnit.SECONDS);
	Assertions.assertTrue(builder.toString().contains("max-age=1000"));
    }

    @Test
    public void test_preload() {
	HstsBuilder builder = new HstsBuilder();

	builder.maxAge(1000, TimeUnit.SECONDS).preload();
	Assertions.assertTrue(builder.toString().contains("max-age=1000; preload"));
    }

    @Test
    public void test_includeSubDomains() {
	HstsBuilder builder = new HstsBuilder();

	builder.maxAge(1000, TimeUnit.SECONDS).includeSubDomains();
	Assertions.assertTrue(builder.toString().contains("max-age=1000; includeSubDomains"));
    }
}
