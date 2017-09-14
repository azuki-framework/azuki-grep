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
package org.azkfw.grep.cash;

import java.io.File;

/**
 * 
 * @author Kawakicchi
 */
public class CashFile {

	/** ファイル */
	private final File file;
	/** サイズ */
	private final long length;
	/** 最終更新日時 */
	private final long lastModified;
	/** 文字コード */
	private final String charset;
	/** 改行コード */
	private final String lineSeparator;

	/** データ */
	private final String data;

	/**
	 * コンストラクタ
	 * 
	 * @param file ファイル
	 * @param charset 文字コード
	 * @param data データ
	 */
	public CashFile(final File file, final String charset, final String lineSeparator, final String data) {
		this.file = file;
		this.length = file.length();
		this.lastModified = file.lastModified();
		this.charset = charset;
		this.lineSeparator = lineSeparator;

		this.data = data;
	}

	/**
	 * ファイルを取得する。
	 * 
	 * @return ファイル
	 */
	public File getFile() {
		return file;
	}

	/**
	 * ファイルサイズを取得する。
	 * 
	 * @return ファイルサイズ
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 最終更新日時を取得する。
	 * 
	 * @return 最終更新日時
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * 文字コードを取得する。
	 * 
	 * @return 文字コード
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * 改行コードを取得する。
	 * 
	 * @return 改行コード
	 */
	public String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * データを取得する。
	 * 
	 * @return データ
	 */
	public String getSource() {
		return data;
	}

	public boolean isMatch(final File file) {
		return ((file.length() == length && file.lastModified() == lastModified));
	}
}
