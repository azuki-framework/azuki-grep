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
package searcher;

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
import org.azkfw.grep.Grep;
import org.azkfw.grep.cash.CashFile;
import org.azkfw.grep.cash.CashStore;
import org.azkfw.grep.entity.ContainingText;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepMatchWord;
import org.azkfw.grep.util.GrepUtility;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author Kawakicchi
 */
public class GrepSearcher implements Runnable {

	/** Grep */
	private final Grep grep;
	/** Grep condition */
	private final GrepCondition condition;
	/** Listener event */
	private final MyGrepSearcherEvent event;
	/** Listener */
	private final GrepSearcherListener listener;

	/** Cash */
	private final CashStore store;

	private final String systemEncode;

	/**
	 * コンストラクタ
	 * 
	 * @param parent Grep
	 * @param condition Grep condition
	 * @param listener Listener
	 */
	public GrepSearcher(final Grep parent, final GrepCondition condition, final GrepSearcherListener listener) {
		this(parent, condition, listener, null);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param parent Grep
	 * @param condition Grep condition
	 * @param listener Listener
	 * @param store Cash
	 */
	public GrepSearcher(final Grep parent, final GrepCondition condition, final GrepSearcherListener listener, final CashStore store) {
		this.grep = parent;
		this.condition = condition;
		this.event = new MyGrepSearcherEvent(this);
		this.listener = listener;

		this.store = store;
		this.systemEncode = System.getProperty("file.encoding");
	}

	@Override
	public void run() {
		try {
			event.reset();
			listener.grepSearcherStart(event);

			while (!event.isStop()) {
				final File file = listener.grepSearcherGetFile(event);
				if (GrepUtility.isNotNull(file)) {
					search(file);
				} else {
					Thread.sleep(100);
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			listener.grepSearcherEnd(event);
		}
	}

	private static final String REGEX_REPLACE_LINE_SEPARATOR = "\r\n|\r|\n";

	private static final Pattern PTN_RETURN = Pattern.compile("\r\n|\r|\n");

	private static final Pattern PTN_RETURN_CRLF = Pattern.compile("\\r\\n");

	private static final Pattern PTN_RETURN_LF = Pattern.compile("[^\\r]\\n");

	private static final Pattern PTN_RETURN_CR = Pattern.compile("\\r[^\\n]");

	private String rep1(final String src) {
		return src.replaceAll(REGEX_REPLACE_LINE_SEPARATOR, "\n");
	}

	private String rep2(final String src) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length(); i++) {
			char c0 = src.charAt(i);
			if ('\r' == c0) {
				if (i + 1 < src.length()) {
					char c1 = src.charAt(i + 1);
					if ('\n' == c1) {
						i++;
					}
				}
				sb.append('\n');

			} else if ('\n' == c0) {
				sb.append('\n');

			} else {
				sb.append(c0);
			}
		}
		return sb.toString();
	}

	private void search(final File file) {
		try {
			final CashFile cashFile = getFile(file);

			// ----------------------------------------------------
			final String source1 = cashFile.getSource();
			// final String source2 = rep1(source1);
			final String source2 = rep2(source1);

			final List<GrepMatchWord> matchWords = new ArrayList<GrepMatchWord>();

			boolean matchFlag = true;
			int patternIndex = 1;
			final List<ContainingText> containingTexts = condition.getContainingTexts();
			for (final ContainingText containingText : containingTexts) {
				final Pattern pattern = containingText.getPattern();
				if (GrepUtility.isNotNull(pattern)) {
					final Matcher m1 = pattern.matcher(source1);
					final Matcher m2 = pattern.matcher(source2);

					boolean find = false;
					while (m1.find() && m2.find()) { // TODO: 該当チェック
						final String word = source1.substring(m1.start(), m1.end());

						final GrepMatchWord matchWord = new GrepMatchWord(patternIndex, word, m1.start(), m1.end(), m2.start(), m2.end());
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
					public int compare(final GrepMatchWord o1, final GrepMatchWord o2) {
						return o1.getStart() - o2.getStart();
					}
				});

				// line count
				final Matcher m = PTN_RETURN.matcher(source1);
				int lineNo = 1;
				int index = 0;
				int last = 0;
				while (m.find()) {
					final int start = m.start();
					final String line = source1.substring(last, start);

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

				final GrepMatchFile matchFile = new GrepMatchFile(file, file.length(), new Date(file.lastModified()), cashFile.getCharset(),
						cashFile.getLineSeparator(), matchWords);

				listener.grepSearcherMatchFile(matchFile, event);
			} else {
				listener.grepSearcherUnmatchFile(file, event);
			}
			// ----------------------------------------------------

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private CashFile getFile(final File file) throws IOException {
		if (GrepUtility.isNotNull(store)) {
			final CashFile cashFile = store.getFile(file);
			if (GrepUtility.isNotNull(cashFile)) {
				if (cashFile.isMatch(file)) {
					return cashFile;
				}
			}
		}

		String encode = getCharset(file);
		if (GrepUtility.isEmpty(encode)) {
			encode = systemEncode;
		}

		String text = FileUtils.readFileToString(file, encode);
		if (0 < text.length() && text.charAt(0) == 65279) { // BOM UTF-8 marker
			text = text.substring(1);
		}

		// 改行コード取得
		final String lineSeparator = getLineSeparator(text);

		final CashFile cashFile = new CashFile(file, encode, lineSeparator, text);

		if (GrepUtility.isNotNull(store)) {
			store.push(cashFile);
		}

		return cashFile;
	}

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

	/**
	 * ファイル内容からファイルのエンコードを判別する。
	 * 
	 * @param file ファイル
	 * @return エンコード
	 */
	private String getCharset(final File file) {
		String charset = null;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);

			int size;
			final byte[] buf = new byte[4096];
			final UniversalDetector detector = new UniversalDetector(null);
			while ((size = stream.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, size);
			}
			detector.dataEnd();
			charset = detector.getDetectedCharset();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			GrepUtility.release(stream);
		}
		return charset;
	}

	private class MyGrepSearcherEvent implements GrepSearcherEvent {

		private boolean stop;

		private MyGrepSearcherEvent(final GrepSearcher searcher) {
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