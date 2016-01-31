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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.azkfw.grep.cash.CashFile;
import org.azkfw.grep.cash.CashStore;
import org.azkfw.grep.entity.ContainingText;
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepMatchWord;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author Kawakicchi
 *
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

	private void search(final File file) {
		try {

			CashFile cashFile = store.getFile(file);
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
			}

			// ----------------------------------------------------
			boolean matchFlag = true;
			List<GrepMatchWord> matchWords = new ArrayList<GrepMatchWord>();
			List<ContainingText> containingTexts = condition.getContainingTexts();
			for (ContainingText containingText : containingTexts) {
				String value = containingText.getValue();
				if (null != value && 0 < value.length()) {
					boolean find = false;
					Matcher m = Pattern.compile(value, Pattern.CASE_INSENSITIVE).matcher(cashFile.getSource());
					while (m.find()) {
						String word = cashFile.getSource().substring(m.start(), m.end());
						GrepMatchWord matchWord = new GrepMatchWord(word, m.start(), m.end());
						matchWords.add(matchWord);
						find = true;
					}
					if (!find) {
						matchFlag = false;
						break;
					}
				}
			}

			if (matchFlag) {
				GrepMatchFile matchFile = new GrepMatchFile(file, cashFile.getCharset(), matchWords);
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