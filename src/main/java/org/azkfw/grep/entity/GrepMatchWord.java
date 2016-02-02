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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * このクラスは、Grepでマッチしたワード情報を保持するエンティティクラスです。
 * 
 * @author Kawakicchi
 */
@XmlType(propOrder={"line", "start", "end", "pattern"})
public class GrepMatchWord implements DocumentPosition{

	private int pattern;

	/** ワード */
	private String word;

	private int line;

	/** 開始位置 */
	private int start;

	/** 終了位置 */
	private int end;

	public GrepMatchWord(final int pattern, final String word, final int start, final int end) {
		this.pattern = pattern;
		this.word = word;
		this.line = 0;
		this.start = start;
		this.end = end;
	}

	public void setPattern(final int pattern) {
		this.pattern = pattern;
	}

	@XmlAttribute(name="pattern")
	public int getPattern() {
		return pattern;
	}

	public void setWord(final String word) {
		this.word = word;
	}

	@XmlValue
	public String getWord() {
		return word;
	}

	public void setLine(final int line) {
		this.line = line;
	}

	@XmlAttribute(name="line")
	public int getLine() {
		return line;
	}

	public void setStart(final int start) {
		this.start = start;
	}

	@Override
	@XmlAttribute(name="start")
	public int getStart() {
		return start;
	}

	public void setEnd(final int end) {
		this.end = end;
	}

	@Override
	@XmlAttribute(name="end")
	public int getEnd() {
		return end;
	}
}
