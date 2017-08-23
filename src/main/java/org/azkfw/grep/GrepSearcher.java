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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.azkfw.grep.cash.CashFile;
import org.azkfw.grep.cash.CashStore;
import org.azkfw.grep.entity.ContainingText;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepMatchWord;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author Kawakicchi
 */
public class GrepSearcher implements Runnable {

	private Grep grep;

	private GrepCondition condition;

	private CashStore store;

	public GrepSearcher(final Grep parent, final GrepCondition condition, final CashStore store) {
		grep = parent;
		this.condition = condition;
		this.store = store;
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

	private static final Pattern PTN_RETURN = Pattern.compile("\\r\\n|\\r|\\n");

	private static final Pattern PTN_RETURN_CRLF = Pattern.compile("\\r\\n");

	private static final Pattern PTN_RETURN_LF = Pattern.compile("[^\\r]\\n");

	private static final Pattern PTN_RETURN_CR = Pattern.compile("\\r[^\\n]");

	private String getLineSeparator(final String source) {
		String lineSeparator = null;
		if (PTN_RETURN_CRLF.matcher(source).find()) {
			// System.out.println("CRLF");
			lineSeparator = "\r\n";
		}
		if (PTN_RETURN_LF.matcher(source).find()) {
			// System.out.println("LF");
			if (null == lineSeparator) {
				lineSeparator = "\n";
			} else {
				lineSeparator = null;
			}
		}
		if (PTN_RETURN_CR.matcher(source).find()) {
			// System.out.println("CR");
			if (null == lineSeparator) {
				lineSeparator = "\r";
			} else {
				lineSeparator = null;
			}
		}
		return lineSeparator;
	}

	private void search(final File file) {
		try {

			CashFile cashFile = store.getFile(file);
			if (null == cashFile) {
				String encode = getCharset(file);
				if (encode == null) {
					encode = System.getProperty("file.encoding");
				}
				String text = FileUtils.readFileToString(file, encode);
				if (0 < text.length() && text.charAt(0) == 65279) {// UTF-8 marker
					text = text.substring(1);
				}
				cashFile = new CashFile(file, text, encode);
			}

			// ----------------------------------------------------
			String source1 = cashFile.getSource();
			String source2 = source1.replaceAll("\r\n|\r|\n", "\n");

			// 改行コード取得
			String lineSeparator = getLineSeparator(source1);

			List<GrepMatchWord> matchWords = new ArrayList<GrepMatchWord>();

			boolean matchFlag = true;
			int patternIndex = 1;
			List<ContainingText> containingTexts = condition.getContainingTexts();
			for (ContainingText containingText : containingTexts) {
				Pattern pattern = containingText.getPattern();
				if (null != pattern) {
					boolean find = false;
					Matcher m1 = pattern.matcher(source1);
					Matcher m2 = pattern.matcher(source2);
					while (m1.find() && m2.find()) { // TODO: 該当チェック
						String word = source1.substring(m1.start(), m1.end());
						GrepMatchWord matchWord = new GrepMatchWord(patternIndex, word, m1.start(), m1.end(), m2.start(), m2.end());
						matchWords.add(matchWord);
						find = true;
					}
					if (!find) {
						matchFlag = false;
						break;
					}
				}
				patternIndex++;
			}

			if (matchFlag) {
				// sort
				Collections.sort(matchWords, new Comparator<GrepMatchWord>() {
					@Override
					public int compare(GrepMatchWord o1, GrepMatchWord o2) {
						return o1.getStart() - o2.getStart();
					}
				});

				// line count
				Matcher m = PTN_RETURN.matcher(source1);
				int lineNo = 1;
				int index = 0;
				int last = 0;
				while (m.find()) {
					int start = m.start();
					String line = source1.substring(last, start);
					for (int i = index; i < matchWords.size(); i++) {
						GrepMatchWord w = matchWords.get(i);
						if (w.getStart() < start) {
							w.setLine(lineNo, last, line);
							index++;
						} else {
							break;
						}
					}
					last = m.end();
					lineNo++;
				}
				String line = source1.substring(last);
				for (int i = index; i < matchWords.size(); i++) {
					matchWords.get(i).setLine(lineNo, last, line);
				}

				GrepMatchFile matchFile = new GrepMatchFile(file,
															file.length(),
															new Date(file.lastModified()),
															cashFile.getCharset(),
															lineSeparator,
															matchWords);
				grep.findFile(matchFile);
			}
			// ----------------------------------------------------

			store.push(cashFile);

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