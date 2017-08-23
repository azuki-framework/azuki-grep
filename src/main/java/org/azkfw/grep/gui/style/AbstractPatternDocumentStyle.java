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
package org.azkfw.grep.gui.style;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * @author Kawakicchi
 */
public abstract class AbstractPatternDocumentStyle extends AbstractDocumentStyle {

	private final List<DocumentStylePattern> patterns;

	public AbstractPatternDocumentStyle() {
		patterns = new ArrayList<DocumentStylePattern>();
	}

	public final void addDocumentStylePattern(final DocumentStylePattern pattern) {
		patterns.add(pattern);
	}

	protected final void doApply(final StyledDocument doc) throws BadLocationException {
		final String source = doc.getText(0, doc.getLength());

		patterns.forEach(pattern -> apply(source, pattern, doc));
	}

	private void apply(final String source, final DocumentStylePattern pattern, final StyledDocument styleDocument) {

		final AttributeSet attributeSet = pattern.getAttributeSet();
		final int group = pattern.getGroup();
		final boolean replace = pattern.isReplace();
		final Matcher m = pattern.getPattern().matcher(source);
		int pos = 0;
		while (m.find(pos)) {
			styleDocument.setCharacterAttributes(m.start(group), m.end(group) - m.start(group), attributeSet, replace);
			pos = m.end(group);
		}
	}

	protected final String getKeywordGroup(final List<String> keywords) {
		StringBuffer s = new StringBuffer();
		for (String keyword : keywords) {
			if (0 < s.length()) {
				s.append("|");
			}
			s.append(keyword);
		}
		return s.toString();
	}

	protected final List<String> getStringList(final String name, final String charset) {
		List<String> strings = new ArrayList<String>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(name), charset));
			String line = null;
			while (null != (line = reader.readLine())) {
				if (0 < line.length()) {
					strings.add(line);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Collections.sort(strings, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.length() < o2.length()) {
					if (o2.startsWith(o1)) {
						return -1;
					}
				} else if (o1.length() > o2.length()) {
					if (o1.startsWith(o2)) {
						return -1;
					}
				}
				return o1.compareTo(o2);
			}
		});
		return strings;
	}
}
