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

package com.appslandia.plum.models;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.common.base.NotBind;
import com.appslandia.common.models.SelectItem;
import com.appslandia.common.models.SelectItemImpl;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.validators.MaxLength;
import com.appslandia.common.validators.ValidValues;
import com.appslandia.plum.base.Resources;

import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ManageSqlModel {

    @NotNull
    @ValidValues(ints = { RESULT_FORMAT_JSON, RESULT_FORMAT_CSV })
    private Integer resultFormat;

    @NotNull
    @ValidValues(ints = { SQL_TYPE_QUERY, SQL_TYPE_UPDATE })
    private Integer sqlType;

    private boolean resultFile;

    @NotNull
    @MaxLength(20000)
    private String sqlText;

    @NotBind
    private List<SelectItem> resultFormats;

    @NotBind
    private List<SelectItem> sqlTypes;

    @NotBind
    private String resultText;

    public boolean isQueryType() {
	Asserts.notNull(this.sqlType);
	return this.sqlType == SQL_TYPE_QUERY;
    }

    public boolean isJsonResult() {
	Asserts.notNull(this.resultFormat);
	return this.resultFormat == RESULT_FORMAT_JSON;
    }

    public Integer getResultFormat() {
	return this.resultFormat;
    }

    public void setResultFormat(Integer resultFormat) {
	this.resultFormat = resultFormat;
    }

    public Integer getSqlType() {
	return this.sqlType;
    }

    public void setSqlType(Integer sqlType) {
	this.sqlType = sqlType;
    }

    public boolean isResultFile() {
	return this.resultFile;
    }

    public void setResultFile(boolean resultFile) {
	this.resultFile = resultFile;
    }

    public String getSqlText() {
	return this.sqlText;
    }

    public void setSqlText(String sqlText) {
	this.sqlText = sqlText;
    }

    public List<SelectItem> getResultFormats() {
	return this.resultFormats;
    }

    public void setResultFormats(List<SelectItem> resultFormats) {
	this.resultFormats = resultFormats;
    }

    public List<SelectItem> getSqlTypes() {
	return this.sqlTypes;
    }

    public void setSqlTypes(List<SelectItem> sqlTypes) {
	this.sqlTypes = sqlTypes;
    }

    public String getResultText() {
	return this.resultText;
    }

    public void setResultText(String resultText) {
	this.resultText = resultText;
    }

    public static final int SQL_TYPE_QUERY = 1;
    public static final int SQL_TYPE_UPDATE = 2;

    public static final int RESULT_FORMAT_JSON = 1;
    public static final int RESULT_FORMAT_CSV = 2;

    public static List<SelectItem> sqlTypes(Resources res) {
	List<SelectItem> list = new ArrayList<>(2);

	list.add(new SelectItemImpl(SQL_TYPE_QUERY, res.getOrDefault("manageSqlModel.sql_type_query", "Query")));
	list.add(new SelectItemImpl(SQL_TYPE_UPDATE, res.getOrDefault("manageSqlModel.sql_type_update", "Update")));

	return list;
    }

    public static List<SelectItem> resultFormats(Resources res) {
	List<SelectItem> list = new ArrayList<>(2);

	list.add(new SelectItemImpl(RESULT_FORMAT_JSON, res.getOrDefault("manageSqlModel.result_format_json", "JSON")));
	list.add(new SelectItemImpl(RESULT_FORMAT_CSV, res.getOrDefault("manageSqlModel.result_format_csv", "CSV")));

	return list;
    }
}
