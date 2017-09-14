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

/**
 * @author Kawakicchi
 *
 */
public interface GrepStatistics {

	/**
	 * 検索で見つけたファイル数
	 * 
	 * @return
	 */
	long getSearchFileCount();

	/**
	 * 検索で見つけたディレクトリ数
	 * 
	 * @return
	 */
	long getSearchDirectoryCount();

	/**
	 * Grep対象ファイル数
	 * 
	 * @return
	 */
	long getTargetFileCount();

	/**
	 * Grepで該当したファイルする。
	 * 
	 * @return
	 */
	long getHitFileCount();

	/**
	 * Grep対象ファイルの総サイズ
	 * 
	 * @return
	 */
	long getTotalTargetFileLength();
}
