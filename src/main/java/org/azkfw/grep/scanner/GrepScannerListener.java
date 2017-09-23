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
package org.azkfw.grep.scanner;

import java.io.File;

/**
 * このインターフェースは、スキャナーのイベントを定義したリスナーインターフェースです。
 * 
 * @author Kawakicchi
 */
public interface GrepScannerListener {

	/**
	 * スキャン開始時に呼び出されるイベント
	 * 
	 * @param event イベント情報
	 */
	void grepScannerStart(GrepScannerEvent event);

	/**
	 * スキャン終了時に呼び出されるイベント
	 * 
	 * @param event イベント情報
	 */
	void grepScannerEnd(GrepScannerEvent event);

	/**
	 * スキャン対象ファイル検出時に呼び出されるイベント
	 * 
	 * @param file ファイル
	 * @param event イベント情報
	 */
	void grepScannerFindFile(File file, GrepScannerEvent event);

	/**
	 * スキャン対象ディレクトリ検出時に呼び出されるイベント
	 * 
	 * @param file ディレクトリ
	 * @param event イベント情報
	 */
	void grepScannerFindDirectory(File directory, GrepScannerEvent event);

	/**
	 * 条一致ファイル検出時に呼び出されるイベント
	 * 
	 * @param file ファイル
	 * @param event イベント情報
	 */
	void grepScannerTargetFile(File file, GrepScannerEvent event);

	/**
	 * 条件一致ディレクトリ検出時に呼び出されるイベント
	 * 
	 * @param file ディレクトリ
	 * @param event イベント情報
	 */
	void grepScannerTargetDirectory(File directory, GrepScannerEvent event);
}
