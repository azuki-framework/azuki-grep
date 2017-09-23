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
@XmlType(propOrder = { "line", "start", "end", "pattern" })
public class GrepMatchWord implements DocumentPosition {

	/** パターンNo */
	private int pattern;

	/** ワード */
	private String word;

	/** 行番号 */
	private int lineNo;

	/** 行開始位置 */
	private int lineStart;

	/** 行文字列 */
	private String lineString;

	/** 開始位置 */
	private int start;

	/** 終了位置 */
	private int end;

	/** 開始位置(改行コードをLFにしたもの) */
	private int virtualStart;
	/** 終了位置(改行コードをLFにしたもの) */
	private int virtualEnd;

	/**
	 * コンストラクタ
	 * 
	 * @param pattern
	 * @param word
	 * @param start
	 * @param end
	 * @param virtualStart
	 * @param virtualEnd
	 */
	public GrepMatchWord(final int pattern, final String word, final int start, final int end, final int virtualStart, final int virtualEnd) {
		this.pattern = pattern;
		this.word = word;
		this.lineNo = 0;
		this.lineString = null;
		this.start = start;
		this.end = end;
		this.virtualStart = virtualStart;
		this.virtualEnd = virtualEnd;
	}

	public void setPattern(final int pattern) {
		this.pattern = pattern;
	}

	@XmlAttribute(name = "pattern")
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

	public void setLine(final int no, final int start, final String string) {
		this.lineNo = no;
		this.lineStart = start;
		this.lineString = string;
	}

	@XmlAttribute(name = "line")
	public int getLine() {
		return lineNo;
	}

	public int getLineStart() {
		return lineStart;
	}

	public String getLineString() {
		return lineString;
	}

	public void setStart(final int start) {
		this.start = start;
	}

	@Override
	@XmlAttribute(name = "start")
	public int getStart() {
		return start;
	}

	public void setEnd(final int end) {
		this.end = end;
	}

	@Override
	@XmlAttribute(name = "end")
	public int getEnd() {
		return end;
	}

	public int getVirtualStart() {
		return virtualStart;
	}

	public int getVirtualEnd() {
		return virtualEnd;
	}
}
