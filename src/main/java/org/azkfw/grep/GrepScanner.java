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
package org.azkfw.grep;

import java.io.File;
import java.util.List;

import org.azkfw.grep.entity.DirectoryNamePattern;
import org.azkfw.grep.entity.FileNamePattern;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.util.GrepUtility;

/**
 * このクラスは、Grep対象のファイルをスキャンするクラスです。
 * 
 * @author Kawakicchi
 */
public class GrepScanner implements Runnable {

	/** Grep */
	private final Grep grep;
	/** Grep condition */
	private final GrepCondition condition;

	/**
	 * コンストラクタ
	 * 
	 * @param parent Grep
	 * @param condition Grep condition
	 */
	public GrepScanner(final Grep parent, final GrepCondition condition) {
		this.grep = parent;
		this.condition = condition;
	}

	@Override
	public void run() {
		final List<File> files = condition.getTargetDirectoryFiles();
		for (final File file : files) {
			if (file.isFile()) {
				doFile(file);
			} else if (file.isDirectory()) {
				doDirectory(file);
			}
		}
	}

	private boolean doFile(final File file) {
		grep.searchFile(file);

		final String name = file.getName();

		// Check exclude file
		final List<FileNamePattern> excludes = condition.getExcludeFileNamePatterns();
		for (FileNamePattern exclude : excludes) {
			if (exclude.getPattern().matcher(name).matches()) {
				return false;
			}
		}

		final List<FileNamePattern> includes = condition.getFileNamePatterns();
		if (GrepUtility.isEmpty(includes)) {
			grep.offerFile(file);
		} else {
			for (final FileNamePattern include : includes) {
				if (include.getPattern().matcher(name).matches()) {
					grep.offerFile(file);
					break;
				}
			}
		}
		return true;
	}

	private boolean doDirectory(final File directory) {
		grep.searchDirectory(directory);

		final String name = directory.getName();

		// Check Excluce directory
		final List<DirectoryNamePattern> excludes = condition.getExcludeDirectoryNamePatterns();
		for (DirectoryNamePattern exclude : excludes) {
			if (exclude.getPattern().matcher(name).matches()) {
				return false;
			}
		}

		final File[] files = directory.listFiles();
		for (final File file : files) {
			if (file.isFile()) {
				doFile(file);
			} else if (file.isDirectory()) {
				doDirectory(file);
			}
		}
		return true;
	}
}