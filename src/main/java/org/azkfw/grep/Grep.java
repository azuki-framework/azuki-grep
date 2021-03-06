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
import org.azkfw.grep.entity.BasicGrepStatistics;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepResult;
import org.azkfw.grep.entity.GrepStatistics;
import org.azkfw.grep.scanner.GrepScanner;
import org.azkfw.grep.scanner.GrepScannerEvent;
import org.azkfw.grep.scanner.GrepScannerListener;

import searcher.GrepSearcher;
import searcher.GrepSearcherEvent;
import searcher.GrepSearcherListener;

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

	/** 統計情報 */
	private final BasicGrepStatistics statistics;
	/** キャッシュ */
	private final CashStore store;

	/** 実行フラグ */
	private Boolean runningFlag;
	/** 停止要求フラグ */
	private Boolean stopRequest;

	/** Grep条件情報 */
	private GrepCondition condition;

	/** マッチファイル一覧 */
	private List<GrepMatchFile> matchFiles;

	private int searcherSize = 2;

	/**
	 * コンストラクタ
	 */
	public Grep() {
		this(null);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param store キャッシュ
	 */
	public Grep(final CashStore store) {
		this.event = new GrepEvent(this);
		this.listeners = new ArrayList<GrepListener>();

		this.statistics = new BasicGrepStatistics();
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

		scanner = new Thread(new GrepScanner(this, condition, new GrepScannerListener() {
			@Override
			public void grepScannerStart(GrepScannerEvent event) {

			}

			@Override
			public void grepScannerEnd(GrepScannerEvent event) {

			}

			@Override
			public void grepScannerFindFile(final File file, final GrepScannerEvent event) {
				if (stopRequest) {
					event.stop();
				}
				searchFile(file);
			}

			@Override
			public void grepScannerFindDirectory(final File file, final GrepScannerEvent event) {
				if (stopRequest) {
					event.stop();
				}
				searchDirectory(file);
			}

			@Override
			public void grepScannerTargetFile(final File file, final GrepScannerEvent event) {
				offerFile(file);
			}

			@Override
			public void grepScannerTargetDirectory(final File file, final GrepScannerEvent event) {
			}
		}));
		scanner.setName("AzukiGrepScannerThread");
		scanner.start();

		searchers = new ArrayList<Thread>();
		for (int i = 0; i < searcherSize; i++) {
			Thread searcher = new Thread(new GrepSearcher(this, condition, new GrepSearcherListener() {
				@Override
				public void grepSearcherStart(GrepSearcherEvent event) {

				}

				@Override
				public void grepSearcherEnd(GrepSearcherEvent event) {

				}

				@Override
				public File grepSearcherGetFile(GrepSearcherEvent event) {
					if (stopRequest) {
						event.stop();
						return null;
					}
					if (isSearcherStop()) {
						event.stop();
						return null;
					}
					return pollFile();
				}

				@Override
				public void grepSearcherMatchFile(final GrepMatchFile matchFile, final GrepSearcherEvent event) {
					findFile(matchFile);
				}

				@Override
				public void grepSearcherUnmatchFile(final File file, final GrepSearcherEvent event) {

				}
			}, store));
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
	synchronized void searchFile(final File file) {
		statistics.countupSearchFile(file);
	}

	/**
	 * Grep対象有無に関わらず見つけたディレクトリ
	 * 
	 * @param directory ディレクトリ
	 */
	synchronized void searchDirectory(final File directory) {
		statistics.countupSearchDirectory(directory);
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
			statistics.countupHitFile(matchFile.getFile());

			listeners.forEach(l -> l.grepFindFile(event, matchFile));
		}
	}

	/**
	 * スキャナー停止確認
	 * <p>
	 * スキャナーが定義済みかの判断を行う。<br/>
	 * スキャナーが停止済みかつ、処理対象ファイルが0件の場合
	 * </p>
	 * 
	 * @return 停止の場合、<code>true</code>を返す。
	 */
	boolean isSearcherStop() {
		if (scanner.isAlive())
			return false;
		if (0 < files.size())
			return false;
		return true;
	}

}
