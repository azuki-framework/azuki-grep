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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Kawakicchi
 */
public abstract class AbstractDocumentStyle implements DocumentStyle{

	private boolean emphasisFlag;
	
	public abstract boolean isSupport(final File file);
	
	public AbstractDocumentStyle() {
		emphasisFlag = false;
	}
	
	public void setEmphasis(final boolean flag) {
		emphasisFlag = flag;
	}
	
	public final boolean isEmphasis() {
		return emphasisFlag;
	}
	
	@Override
	public void apply(final StyledDocument doc) throws BadLocationException {
		doApply(doc);
	}
		
	protected abstract void doApply(final StyledDocument doc) throws BadLocationException ;
	
	protected final String getKeywordGroup(final List<String> keywords) {
		StringBuffer s = new StringBuffer();
		s.append("(");
		for (String keyword : keywords) {
			if (1 < s.length()) {
				s.append("|");
			}
			s.append(keyword);
		}
		s.append(")");
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
