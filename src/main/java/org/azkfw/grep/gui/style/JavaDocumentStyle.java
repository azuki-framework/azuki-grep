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

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author Kawakicchi
 */
public class JavaDocumentStyle extends AbstractPatternDocumentStyle {

	private static final Pattern PTN_FILE = Pattern.compile("^.*\\.java$", Pattern.CASE_INSENSITIVE);

	public JavaDocumentStyle() {
		final List<String> keywordList = getStringList("/org/azkfw/grep/java_keyword.txt", "UTF-8");
		final String keywords = getKeywordGroup(keywordList);

		final String regKeywords = String.format("(^|[,;\\(\\)\\[\\]\\s\\t\\r\\n])(%s)([,;\\(\\)\\[\\]\\s\\t\\r\\n]|$)", keywords);
		final Pattern PTN_KEYWORD = Pattern.compile(regKeywords, Pattern.CASE_INSENSITIVE);

		final Pattern PTN_STRING = Pattern.compile("(\"([^\"]*)\")");

		final Pattern PTN_KIGO = Pattern.compile("([\\,\\(\\)\\{\\}\\*\\+\\-=<>\\|])");

		final Pattern PTN_NUMBER = Pattern.compile("([\\s\\,\\(\\)\\[\\]\\+\\-]|^|\\n)([0-9\\.]+)([\\s\\,\\(\\)\\[\\]\\+\\-]|$|\\n)");

		final Pattern PTN_COMMENT1 = Pattern.compile("(//.*)");
		final Pattern PTN_COMMENT2 = Pattern.compile("(/\\*([^*]|\\*[^/])*\\*/)", Pattern.DOTALL);

		final Pattern PTN_JAVADOC = Pattern.compile("(/\\*\\*([^*]|\\*[^/])*\\*/)", Pattern.DOTALL);

		final SimpleAttributeSet ATTR_KEYWORD = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTR_KEYWORD, new Color(127, 0, 85));
		StyleConstants.setBold(ATTR_KEYWORD, true);

		final SimpleAttributeSet ATTR_VALUE = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTR_VALUE, new Color(200, 0, 0));

		final SimpleAttributeSet ATTR_KIGO = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTR_KIGO, new Color(140, 0, 0));

		final SimpleAttributeSet ATTR_COMMENT = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTR_COMMENT, new Color(63, 127, 95));

		final SimpleAttributeSet ATTR_JAVADOC = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTR_JAVADOC, new Color(63, 95, 191));

		addDocumentStylePattern(new DocumentStylePattern(PTN_KIGO, 1, ATTR_KIGO));
		addDocumentStylePattern(new DocumentStylePattern(PTN_NUMBER, 2, ATTR_VALUE));
		addDocumentStylePattern(new DocumentStylePattern(PTN_KEYWORD, 2, ATTR_KEYWORD));
		addDocumentStylePattern(new DocumentStylePattern(PTN_STRING, 1, ATTR_VALUE));

		addDocumentStylePattern(new DocumentStylePattern(PTN_COMMENT1, 1, ATTR_COMMENT, true));
		addDocumentStylePattern(new DocumentStylePattern(PTN_COMMENT2, 1, ATTR_COMMENT, true));

		addDocumentStylePattern(new DocumentStylePattern(PTN_JAVADOC, 1, ATTR_JAVADOC, true));
	}

	public boolean isSupport(final File file) {
		return PTN_FILE.matcher(file.getName()).matches();
	}

}
