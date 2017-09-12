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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.azkfw.grep.cash.CashStore;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepResult;
import org.azkfw.grep.entity.GrepStatistics;

/**
 * このクラスは、Grepを行うクラスです。
 * 
 * @author Kawakicchi
 */
public class Grep {

	/** Grep event info */
	private final GrepEvent event;
	/** Grep event listeners */
	private final List<GrepListener> listeners;

	private final MyGrepStatistics statistics;

	private final CashStore store;

	private Boolean runningFlag;
	@SuppressWarnings("unused")
	private Boolean stopRequest;

	private GrepCondition condition;

	private List<GrepMatchFile> matchFiles;

	private int searcherSize = 2;

	public Grep() {
		this(null);
	}

	public Grep(final CashStore store) {
		this.event = new GrepEvent(this);
		this.listeners = new ArrayList<GrepListener>();

		this.statistics = new MyGrepStatistics();
		this.store = store;

		runningFlag = Boolean.FALSE;
		stopRequest = Boolean.FALSE;

		matchFiles = new ArrayList<GrepMatchFile>();
	}

	public synchronized void addGrepListener(final GrepListener listener) {
		listeners.add(listener);
	}

	public GrepStatistics getStatistics() {
		return statistics;
	}

	public GrepCondition getCondition() {
		return condition;
	}

	public boolean start(final GrepCondition condition) {
		boolean result = false;

		synchronized (this) {

			if (!runningFlag) {
				this.condition = condition;

				runningFlag = Boolean.TRUE;
				stopRequest = Boolean.FALSE;

				final Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						matchFiles.clear();

						// call listener start
						synchronized (listeners) {
							listeners.forEach(l -> l.grepStart(event));
						}

						final long startNanoTime = System.nanoTime();

						doThreadMain();

						runningFlag = Boolean.FALSE;

						final long endNanoTime = System.nanoTime();

						final GrepResult result = new GrepResult();
						result.setProcessingNanoTime(endNanoTime - startNanoTime);
						result.setMatchFiles(matchFiles);

						// call listener finished
						synchronized (listeners) {
							listeners.forEach(l -> l.grepFinished(event, result));
						}
					}
				});
				thread.setName("AzukiGrepMainThread");
				thread.start();

				result = true;
			}
		}

		return result;
	}

	public void stop() {
		stopRequest = Boolean.TRUE;
	}

	public boolean waitFor() {
		boolean result = false;
		try {
			while (runningFlag) {
				Thread.sleep(100);
			}
			result = true;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private Thread scanner;
	private List<Thread> searchers;

	private Queue<File> files;

	private void doThreadMain() {
		statistics.reset();

		files = new LinkedList<File>();

		scanner = new Thread(new GrepScanner(this, condition));
		scanner.setName("AzukiGrepScannerThread");
		scanner.start();

		searchers = new ArrayList<Thread>();
		for (int i = 0; i < searcherSize; i++) {
			Thread searcher = new Thread(new GrepSearcher(this, condition, store));
			searcher.setName("AzukiGrepSearcherThread-" + (i + 1));
			searchers.add(searcher);
		}
		for (Thread thread : searchers) {
			thread.start();
		}

		while (!isStop()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private boolean isStop() {
		if (scanner.isAlive()) {
			return false;
		}
		for (Thread searcher : searchers) {
			if (searcher.isAlive()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Grep対象有無に関わらず見つけたファイル
	 * 
	 * @param file ファイル
	 */
	void searchFile(final File file) {
		statistics.countupSearchFile(file);
	}

	/**
	 * Grep対象有無に関わらず見つけたディレクトリ
	 * 
	 * @param file ファイル
	 */
	void searchDirectory(final File directory) {

	}

	/**
	 * Grep対象のファイルをキューに入れる。
	 * 
	 * @param file ファイル
	 */
	void offerFile(final File file) {
		synchronized (files) {
			statistics.countupTargetFile(file);

			files.offer(file);
		}
	}

	/**
	 * Grep対象のファイルをキューから取得する。
	 * 
	 * @return
	 */
	File pollFile() {
		File file = null;
		synchronized (files) {
			file = files.poll();
		}
		return file;
	}

	void findFile(final GrepMatchFile matchFile) {
		// call listener finished
		synchronized (listeners) {
			matchFiles.add(matchFile);
			statistics.countupFindFile(matchFile.getFile());

			listeners.forEach(l -> l.grepFindFile(event, matchFile));
		}
	}

	boolean isSearcherStop() {
		if (scanner.isAlive())
			return false;
		if (0 < files.size())
			return false;
		return true;
	}

	private class MyGrepStatistics implements GrepStatistics {

		/** トータルファイル数 */
		private long totalSearchFile;
		/** Grep対象ファイル数 */
		private long totalTargetFile;
		/** 該当ファイル数 */
		private long totalFindFile;

		public void reset() {
			totalSearchFile = 0;
			totalTargetFile = 0;
			totalFindFile = 0;
		}

		void countupSearchFile(final File file) {
			totalSearchFile++;
		}

		void countupTargetFile(final File file) {
			totalTargetFile++;
		}

		void countupFindFile(final File file) {
			totalFindFile++;
		}

		public void print() {
			System.out.println(String.format("SearchFile : %d", totalSearchFile));
			System.out.println(String.format("TargetFile : %d", totalTargetFile));
			System.out.println(String.format("FindFile   : %d", totalFindFile));
		}

		@Override
		public long getFindFileCount() {
			return totalFindFile;
		}
	}
}
