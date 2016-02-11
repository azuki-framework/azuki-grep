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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Kawakicchi
 */
public class SQLDocumentStyle extends AbstractDocumentStyle {

	private static final Pattern PTN_FILE = Pattern.compile("^.*\\.sql$", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern PTN_COMMENT1 = Pattern.compile("(\\/\\*.*?\\*\\/)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	private static final Pattern PTN_COMMENT2 = Pattern.compile("(--[^\\r\\n]*)");
	private static final Pattern PTN_STRING = Pattern.compile("(('[^']*')|(\"((\\\\\")|[^\"])*\"))");
	
	private static Pattern PTN_KEYWORD;
	private static Pattern PTN_FUNCTION;

	private static Boolean LOAD = false;

	public SQLDocumentStyle() {
		synchronized (LOAD) {
			if (!LOAD) {
				LOAD = true;

				List<String> keywordList = getStringList("/org/azkfw/grep/sql_keyword.txt", "UTF-8");
				String keywords = getKeywordGroup(keywordList);
				PTN_KEYWORD = Pattern.compile("(^|[,;\\(\\)\\s\\t\\r\\n]){1,1}" + keywords + "{1,1}([,;\\(\\)\\s\\t\\r\\n]|$){1,1}", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
				
				List<String> functionList = getStringList("/org/azkfw/grep/sql_function.txt", "UTF-8");
				String functions = getKeywordGroup(functionList);
				PTN_FUNCTION = Pattern.compile("(^|[,\\(\\)\\s\\t\\r\\n]){1,1}" + functions + "{1,1}([,\\s\\t\\r\\n])*(\\(){1,1}", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
			}
		}
	}

	public boolean isSupport(final File file) {
		return PTN_FILE.matcher(file.getName()).matches();
	}

	protected void doApply(final StyledDocument doc) throws BadLocationException {
		SimpleAttributeSet attrKeyword = new SimpleAttributeSet();
        StyleConstants.setForeground(attrKeyword, new Color(128,0,64));  // 文字の色
		SimpleAttributeSet attrComment = new SimpleAttributeSet();
        StyleConstants.setForeground(attrComment, new Color(0, 100, 0));  // 文字の色
		SimpleAttributeSet attrFunction = new SimpleAttributeSet();
        StyleConstants.setForeground(attrFunction, new Color(50, 50, 200));  // 文字の色
		SimpleAttributeSet attrString = new SimpleAttributeSet();
        StyleConstants.setForeground(attrString, new Color(255, 0, 0));  // 文字の色

        if (isEmphasis()) {
        	StyleConstants.setBold(attrKeyword, true);
        	StyleConstants.setBold(attrComment, true);
        	StyleConstants.setBold(attrFunction, true);
        }

		String source = doc.getText(0, doc.getLength());

		int index = 0;
		
		Matcher m = null;
		m = PTN_KEYWORD.matcher(source);
		while (m.find(index)) {
			 doc.setCharacterAttributes(m.start(2), m.end(2)-m.start(2), attrKeyword, true);
			 index = m.start(3);
		}
		index = 0;
		m = PTN_FUNCTION.matcher(source);
		while (m.find(index)) {
			 doc.setCharacterAttributes(m.start(2), m.end(2)-m.start(2), attrFunction, true);
			 index = m.start(4);
		}
		m = PTN_STRING.matcher(source);
		while (m.find()) {
			 doc.setCharacterAttributes(m.start(), m.end()-m.start(), attrString, true);
		}
		m = PTN_COMMENT2.matcher(source);
		while (m.find()) {
			 doc.setCharacterAttributes(m.start(), m.end()-m.start(), attrComment, true);
		}
		m = PTN_COMMENT1.matcher(source);
		while (m.find()) {
			 doc.setCharacterAttributes(m.start(), m.end()-m.start(), attrComment, true);
		}
	}
}
