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

package com.appslandia.plum.tags;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.utils.DateUtils;

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
			Assert.assertEquals(v, "BTC");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_lower() {
		try {
			Object v = Pipe.transform("JAVAEE-7", "lower");
			Assert.assertEquals(v, "javaee-7");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_trunc() {
		try {
			Object v = Pipe.transform("123456789", "trunc:4");
			Assert.assertEquals(v, "1234...");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_fmtDate() {
		try {
			Object v = Pipe.transform(DateUtils.iso8601Date("2018-06-07"), "fmtDate:MM/dd/yyyy");
			Assert.assertEquals(v, "06/07/2018");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}

		try {
			Object v = Pipe.transform(DateUtils.iso8601LocalDate("2018-06-07"), "fmtDate:MM/dd/yyyy");
			Assert.assertEquals(v, "06/07/2018");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_fmtGroup() {
		try {
			Object v = Pipe.transform("4028888888", "fmtGroup:{3}-{3}-{4}");
			Assert.assertEquals(v, "402-888-8888");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_fmtNumber() {
		try {
			Object v = Pipe.transform(123, "fmtNumber:%05d");
			Assert.assertEquals(v, "00123");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_maskStart() {
		try {
			Object v = Pipe.transform("0123456789", "maskStart:5");
			Assert.assertEquals(v, "*****56789");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_maskEnd() {
		try {
			Object v = Pipe.transform("0123456789", "maskEnd:5");
			Assert.assertEquals(v, "01234*****");
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
