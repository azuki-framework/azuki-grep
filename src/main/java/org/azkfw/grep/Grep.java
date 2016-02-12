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
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepResult;
import org.azkfw.grep.entity.GrepStatistics;
import org.azkfw.grep.gui.GrepFrame;

/**
 * 
 * @author Kawakicchi
 */
public class Grep {
	
	public static void main(final String[] args) {
		GrepFrame frm = new GrepFrame();
		frm.setVisible(true);
	}

	private GrepEvent event;
	private List<GrepListener> listeners;

	private Boolean runningFlag;
	@SuppressWarnings("unused")
	private Boolean stopRequest;

	private GrepCondition condition;
	private BaseGrepStatistics statistics;
	
	private CashStore store;
	
	private List<GrepMatchFile> matchFiles;

	public Grep() {
		event = new GrepEvent(this);
		listeners = new ArrayList<GrepListener>();

		runningFlag = Boolean.FALSE;
		stopRequest = Boolean.FALSE;

		statistics = new BaseGrepStatistics();
		store = new CashStore();
		
		matchFiles = new ArrayList<GrepMatchFile>();
	}
	
	public GrepStatistics getStatistics() {
		return statistics;
	}
	
	public GrepCondition getCondition() {
		return condition;
	}

	public void addGrepListener(final GrepListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public boolean start(final GrepCondition condition) {
		boolean result = false;

		synchronized (runningFlag) {
			if (!runningFlag) {
				this.condition = condition;
				
				runningFlag = Boolean.TRUE;
				stopRequest = Boolean.FALSE;

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						matchFiles.clear();
						
						// call listener start
						synchronized (listeners) {
							for (GrepListener listener : listeners) {
								listener.grepStart(event);
							}
						}

						long startNanoTime = System.nanoTime();

						doThreadMain();

						runningFlag = Boolean.FALSE;

						long endNanoTime = System.nanoTime();

						GrepResult result = new GrepResult();
						result.setProcessingNanoTime(endNanoTime - startNanoTime);
						result.setMatchFiles(matchFiles);

						// call listener finished
						synchronized (listeners) {
							for (GrepListener listener : listeners) {
								listener.grepFinished(event, result);
							}
						}
					}
				});
				thread.start();
				result = true;
			}
		}
		return result;
	}

	public void stop() {
		stopRequest = Boolean.TRUE;
	}

	public void waitFor() {
		while (runningFlag) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private Thread scanner;
	private List<Thread> searchers;
	private Queue<File> files;

	private void doThreadMain() {
		statistics.reset();
		files = new LinkedList<File>();

		scanner = new Thread(new GrepScanner(this, condition));
		scanner.start();

		searchers = new ArrayList<Thread>();
		for (int i = 0; i < 5; i++) {
			searchers.add(new Thread(new GrepSearcher(this, condition, store)));
		}
		for (Thread thread : searchers) {
			thread.start();
		}

		while (!isStop()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private boolean isStop() {
		if (scanner.isAlive())
			return false;
		for (Thread searcher : searchers) {
			if (searcher.isAlive())
				return false;
		}
		return true;
	}

	void searchFile(final File file) {
		statistics.countupSearchFile(file);
	}
	
	void offerFile(final File file) {
		synchronized (files) {
			statistics.countupTargetFile(file);
			
			files.offer(file);
		}
	}

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
			
			for (GrepListener listener : listeners) {
				listener.grepFindFile(event, matchFile);
			}
		}
	}

	boolean isSearcherStop() {
		if (scanner.isAlive())
			return false;
		if (0 < files.size())
			return false;
		return true;
	}

	public class BaseGrepStatistics implements GrepStatistics{

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
			totalSearchFile ++;
		}
		void countupTargetFile(final File file) {
			totalTargetFile ++;
		}
		void countupFindFile(final File file) {
			totalFindFile ++;
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
