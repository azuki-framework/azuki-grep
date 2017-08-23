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

/**
 * @author Kawakicchi
 */
public class GrepScanner implements Runnable {

	private Grep grep;

	private GrepCondition condition;

	public GrepScanner(final Grep parent, final GrepCondition condition) {
		grep = parent;
		this.condition = condition;
	}

	public void run() {
		List<File> files = condition.getTargetDirectoryFiles();
		for (File file : files) {
			if (file.isFile()) {
				doFile(file);
			} else if (file.isDirectory()) {
				doDirectory(file);
			}
		}
	}

	private boolean doFile(final File file) {
		grep.searchFile(file);

		String name = file.getName();

		final List<FileNamePattern> excludes = condition.getExcludeFileNamePatterns();
		for (FileNamePattern exclude : excludes) {
			// TODO: 毎回パターンコンパイルしている非効率
			if (exclude.getPattern().matcher(name).matches()) {
				return false;
			}
		}

		List<FileNamePattern> includes = condition.getFileNamePatterns();
		if (0 == includes.size()) {
			grep.offerFile(file);
		} else {
			for (FileNamePattern include : includes) {
				// TODO: 毎回パターンコンパイルしている非効率
				if (include.getPattern().matcher(name).matches()) {
					grep.offerFile(file);
					break;
				}
			}
		}
		return true;
	}

	private boolean doDirectory(final File directory) {
		String name = directory.getName();

		List<DirectoryNamePattern> excludes = condition.getExcludeDirectoryNamePatterns();
		for (DirectoryNamePattern exclude : excludes) {
			// TODO: 毎回パターンコンパイルしている非効率
			if (exclude.getPattern().matcher(name).matches()) {
				return false;
			}
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				doFile(file);
			} else if (file.isDirectory()) {
				doDirectory(file);
			}
		}
		return true;
	}
}