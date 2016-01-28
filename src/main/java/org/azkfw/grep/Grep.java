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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.azkfw.grep.util.FormatUtility;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * 
 * @author Kawakicchi
 */
public class Grep {

	public static void main(final String[] args) {
		GrepCondition condition = new GrepCondition();
		
		Grep grep = new Grep();

		System.out.println("Start");
		grep.start(condition);
		grep.waitFor();
		System.out.println("end");
	}

	private GrepEvent event;
	private List<GrepListener> listeners;

	private Boolean runningFlag;
	private Boolean stopRequest;

	private GrepCondition condition;
	private BaseGrepStatistics statistics;
	
	private CashStore store;

	public Grep() {
		event = new GrepEvent(this);
		listeners = new ArrayList<GrepListener>();

		runningFlag = Boolean.FALSE;
		stopRequest = Boolean.FALSE;

		statistics = new BaseGrepStatistics();
		store = new CashStore();
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

		scanner = new Thread(new Scanner(this, condition));
		scanner.start();

		searchers = new ArrayList<Thread>();
		for (int i = 0; i < 5; i++) {
			searchers.add(new Thread(new Searcher(this, condition)));
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

	private void offerFile(final File file) {
		synchronized (files) {
			files.offer(file);
		}
	}

	private File pollFile() {
		File file = null;
		synchronized (files) {
			file = files.poll();
		}
		return file;
	}
	private void findFile(final FindFile file) {
		// call listener finished
		synchronized (listeners) {
			for (GrepListener listener : listeners) {
				listener.grepFindFile(event, file);
			}
		}
	}

	private boolean isSearcherStop() {
		if (scanner.isAlive())
			return false;
		if (0 < files.size())
			return false;
		return true;
	}

	private class Scanner implements Runnable {

		private Pattern ptn;

		private Grep grep;
		private GrepCondition condition;

		public Scanner(final Grep parent, final GrepCondition condition) {
			ptn = Pattern.compile("^.*\\.java$");
			//ptn = Pattern.compile("^.*\\.[mch]$");

			grep = parent;
			this.condition = condition;
		}

		public void run() {
			File file = condition.getTargetDirectory();
			if (file.isFile()) {
				doFile(file);
			} else if (file.isDirectory()) {
				doDirectory(file);
			}
		}

		private boolean doFile(final File file) {
			grep.statistics.countupSearchFile(file);
			if (ptn.matcher(file.getAbsolutePath()).matches()) {
				grep.statistics.countupTargetFile(file);
				grep.offerFile(file);
			}
			return true;
		}

		private boolean doDirectory(final File directory) {
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

	private class Searcher implements Runnable {

		private Grep grep;
		private GrepCondition condition;

		public Searcher(final Grep parent, final GrepCondition condition) {
			grep = parent;
			this.condition = condition;
		}

		public void run() {
			while (!grep.isSearcherStop()) {
				File file = grep.pollFile();
				if (null != file) {
					search(file);
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		private void search(final File file) {
			try {
				CashFile cashFile = grep.store.getFile(file);
				if (null == cashFile) {
					String encode = getCharset(file);
					if (encode == null) {
						encode = System.getProperty("file.encoding");
					}
					String text = FileUtils.readFileToString(file, encode);
					if (text.charAt(0) == 65279) {// UTF-8 marker
						text = text.substring(1);
					}
					cashFile = new CashFile(file, text, encode);
					grep.statistics.addTotalReadFileSize(cashFile.getLength());
				} else {
					grep.statistics.addTotalCashFileSize(cashFile.getLength());
				}

				// ----------------------------------------------------
				List<GrepMatch> matchs = new ArrayList<GrepMatch>();
				Matcher m = Pattern.compile("String",Pattern.CASE_INSENSITIVE ).matcher(cashFile.getSource());
				while (m.find()) {
					String word = cashFile.getSource().substring(m.start(), m.end());
					GrepMatch match = new GrepMatch(word, m.start(), m.end());
					matchs.add(match);
				}
				if (0 < matchs.size()) {
					grep.statistics.countupFindFile(file);
					
					FindFile findFile = new FindFile(file, cashFile.getCharset(), matchs);
					grep.findFile(findFile);
				}
				// ----------------------------------------------------

				grep.store.push(cashFile);

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		private String getCharset(final File file) {
			String charset = null;
			FileInputStream stream = null;
			try {
				stream = new FileInputStream(file);
				byte[] buf = new byte[4096];
				UniversalDetector detector = new UniversalDetector(null);
				int size;
				while ((size = stream.read(buf)) > 0 && !detector.isDone()) {
					detector.handleData(buf, 0, size);
				}
				detector.dataEnd();
				charset = detector.getDetectedCharset();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != stream) {
					try {
						stream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			return charset;
		}
	}
	
	public class BaseGrepStatistics implements GrepStatistics{

		/** トータルファイル数 */
		private long totalSearchFile;
		/** Grep対象ファイル数 */
		private long totalTargetFile;
		/** 該当ファイル数 */
		private long totalFindFile;
		
		private long totalReadFileSize;
		private long totalCashFileSize;
		
		public void reset() {
			totalSearchFile = 0;
			totalTargetFile = 0;
			totalFindFile = 0;
			
			totalReadFileSize = 0;
			totalCashFileSize = 0;
		}
		
		public void countupSearchFile(final File file) {
			totalSearchFile ++;
		}
		public void countupTargetFile(final File file) {
			totalTargetFile ++;
		}
		public void countupFindFile(final File file) {
			totalFindFile ++;
		}
		
		public void addTotalReadFileSize(final long size) {
			totalReadFileSize += size;
		}
		public void addTotalCashFileSize(final long size) {
			totalCashFileSize += size;
		}
		
		public void print() {
			System.out.println(String.format("SearchFile : %d", totalSearchFile));
			System.out.println(String.format("TargetFile : %d", totalTargetFile));
			System.out.println(String.format("FindFile   : %d", totalFindFile));
			System.out.println(String.format("readSize   : %s", FormatUtility.byteToString(totalReadFileSize)));
			System.out.println(String.format("cashSize   : %s", FormatUtility.byteToString(totalCashFileSize)));
		}
		
		@Override
		public long getFindFileCount() {
			return totalFindFile;
		}
	}
}
