/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.grep.entity;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * このクラスは、Grepでマッチしたファイル情報を保持するエンティティクラスです。
 * 
 * @author Kawakicchi
 */
public class GrepMatchFile {

	private File file;

	private long length;
	
	private Date lastModifiedDate;

	private String charset;

	private String lineSeparator;

	private List<GrepMatchWord> words;

	public GrepMatchFile(final File file, final long length, final Date lastModifiedDate, final String charset, final String lineSeparator, final List<GrepMatchWord> words) {
		this.file = file;
		this.length = length;
		this.lastModifiedDate = lastModifiedDate;
		this.charset = charset;
		this.lineSeparator = lineSeparator;
		this.words = words;
	}

	public File getFile() {
		return file;
	}
	
	@XmlElement(name="Path")
	public String getPath() {
		return file.getAbsolutePath();
	}
	
	@XmlElement(name="Length")
	public long getLength() {
		return length;
	}

	public Date getLastModifedDate() {
		return lastModifiedDate;
	}

	@XmlElement(name="Charset")
	public String getCharset() {
		return charset;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	@XmlElement(name="LineSeparator")
	public String getLineSeparatorToString() {
		if (null == lineSeparator) {
			return "";
		} else if ("\r\n".equals(lineSeparator)) {
			return "CR+LF";
		} else if ("\n".equals(lineSeparator)) {
			return "LF";
		} else if ("\r".equals(lineSeparator)) {
			return "CR";
		}
		return "";
	}

	@XmlElementWrapper(name="MatchWords")
	@XmlElement(name="MatchWord")
	public List<GrepMatchWord> getWords() {
		return words;
	}
}
