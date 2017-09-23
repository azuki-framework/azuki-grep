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
import java.util.List;

import org.azkfw.grep.Grep;
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
	/** Listener event */
	private final MyGrepScannerEvent event;
	/** Listener */
	private final GrepScannerListener listener;

	/**
	 * コンストラクタ
	 * 
	 * @param parent Grep
	 * @param condition Grep condition
	 * @param listener Listener
	 */
	public GrepScanner(final Grep parent, final GrepCondition condition, final GrepScannerListener listener) {
		this.grep = parent;
		this.condition = condition;
		this.event = new MyGrepScannerEvent(this);
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			event.reset();
			listener.grepScannerStart(event);

			final List<File> files = condition.getTargetDirectoryFiles();
			for (final File file : files) {
				if (file.isFile()) {
					doFile(file);
				} else if (file.isDirectory()) {
					doDirectory(file);
				}
			}
		} finally {
			listener.grepScannerEnd(event);
		}
	}

	/**
	 * 
	 * @param file
	 */
	private void doFile(final File file) {
		listener.grepScannerFindFile(file, event);

		final String name = file.getName();

		// Check exclude file
		final List<FileNamePattern> excludes = condition.getExcludeFileNamePatterns();
		for (FileNamePattern exclude : excludes) {
			if (exclude.getPattern().matcher(name).matches()) {
				return;
			}
		}

		final List<FileNamePattern> includes = condition.getFileNamePatterns();
		if (GrepUtility.isEmpty(includes)) {
			listener.grepScannerTargetFile(file, event);

		} else {
			for (final FileNamePattern include : includes) {
				if (include.getPattern().matcher(name).matches()) {
					listener.grepScannerTargetFile(file, event);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param directory
	 */
	private void doDirectory(final File directory) {
		listener.grepScannerFindDirectory(directory, event);

		final String name = directory.getName();

		// Check Excluce directory
		final List<DirectoryNamePattern> excludes = condition.getExcludeDirectoryNamePatterns();
		for (DirectoryNamePattern exclude : excludes) {
			if (exclude.getPattern().matcher(name).matches()) {
				return;
			}
		}

		listener.grepScannerTargetDirectory(directory, event);

		final File[] files = directory.listFiles();
		for (final File file : files) {
			if (file.isFile()) {
				doFile(file);
			} else if (file.isDirectory()) {
				doDirectory(file);
			}
		}
	}

	private class MyGrepScannerEvent implements GrepScannerEvent {

		private boolean stop;

		private MyGrepScannerEvent(final GrepScanner scanner) {
			reset();
		}

		@Override
		public void stop() {
			stop = true;
		}

		private boolean isStop() {
			return stop;
		}

		private void reset() {
			stop = false;
		}
	}
}