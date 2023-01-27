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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CspValueBuilder {

    final Set<String> values = new LinkedHashSet<>();

    public CspValueBuilder any() {
	this.values.add("*");
	return this;
    }

    public CspValueBuilder self() {
	this.values.add("'self'");
	return this;
    }

    public CspValueBuilder none() {
	this.values.add("'none'");
	return this;
    }

    public CspValueBuilder unsafeInline() {
	this.values.add("'unsafe-inline'");
	return this;
    }

    public CspValueBuilder unsafeEval() {
	this.values.add("'unsafe-eval'");
	return this;
    }

    public CspValueBuilder unsafeHashedAttributes() {
	this.values.add("'unsafe-hashed-attributes'");
	return this;
    }

    public CspValueBuilder https() {
	this.values.add("https:");
	return this;
    }

    public CspValueBuilder data() {
	this.values.add("data:");
	return this;
    }

    public CspValueBuilder mediastream() {
	this.values.add("mediastream:");
	return this;
    }

    public CspValueBuilder blob() {
	this.values.add("blob:");
	return this;
    }

    public CspValueBuilder filesystem() {
	this.values.add("filesystem:");
	return this;
    }

    public CspValueBuilder nonce(String value) {
	this.values.add("'nonce-" + value + '\'');
	return this;
    }

    public CspValueBuilder sha256(String value) {
	this.values.add("'sha256-" + value + '\'');
	return this;
    }

    public CspValueBuilder sha386(String value) {
	this.values.add("'sha386-" + value + '\'');
	return this;
    }

    public CspValueBuilder sha512(String value) {
	this.values.add("'sha512-" + value + '\'');
	return this;
    }

    public CspValueBuilder add(String value) {
	this.values.add(value);
	return this;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	for (String val : this.values) {
	    if (sb.length() > 0) {
		sb.append(' ');
	    }
	    sb.append(val);
	}
	return (sb.length() > 0) ? sb.toString() : null;
    }
}
