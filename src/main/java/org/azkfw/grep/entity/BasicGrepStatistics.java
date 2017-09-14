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

/**
 * @author Kawakicchi
 *
 */
public class BasicGrepStatistics implements GrepStatistics {

	private long searchFileCount;
	private long searchDirectoryCount;

	private long targetFileCount;
	private long hitFileCount;
	private long totalTargetFileLength;

	public BasicGrepStatistics() {
		reset();
	}

	@Override
	public long getSearchFileCount() {
		return searchFileCount;
	}

	@Override
	public long getSearchDirectoryCount() {
		return searchDirectoryCount;
	}

	@Override
	public long getTargetFileCount() {
		return targetFileCount;
	}

	@Override
	public long getHitFileCount() {
		return hitFileCount;
	}

	@Override
	public long getTotalTargetFileLength() {
		return totalTargetFileLength;
	}

	public void reset() {
		searchFileCount = 0;
		searchDirectoryCount = 0;

		targetFileCount = 0;
		hitFileCount = 0;
		totalTargetFileLength = 0;
	}

	public void countupSearchFile(final File file) {
		searchFileCount++;
	}

	public void countupSearchDirectory(final File directory) {
		searchDirectoryCount++;
	}

	public void countupTargetFile(final File file) {
		targetFileCount++;
		totalTargetFileLength += file.length();
	}

	public void countupHitFile(final File file) {
		hitFileCount++;
	}

}
