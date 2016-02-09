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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author Kawakicchi
 *
 */
public class SQLStyledDocument extends AbstractStyledDocument {

	private static final String key = "(select|insert|update|from|inner|outer|join|left|right|on|union|all|where|and|or|siblings|group|order|by|asc|desc|nulls|first|last|for|wait|unique|distinct)";
	private static final Pattern PTN_KEYWORD = Pattern.compile("(^|[\\s\\t\\r\\n]){1,1}"+key+"{1,1}([\\s\\t\\r\\n]|$){1,1}", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	private static final Pattern PNT_JAVADOC = Pattern.compile("(\\/\\*\\*.*?\\*\\/)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	private static final Pattern PNT_COMMENT = Pattern.compile("(--[^\\r\\n]*)", Pattern.CASE_INSENSITIVE);
	
	public boolean isSupport(final File file) {
		return file.getName().endsWith(".sql");
	}

	protected void doApply(final StyledDocument doc) throws BadLocationException {
		SimpleAttributeSet attrKeyword = new SimpleAttributeSet();
        StyleConstants.setForeground(attrKeyword, new Color(128,0,64));  // 文字の色
		SimpleAttributeSet attrJavadoc = new SimpleAttributeSet();
        StyleConstants.setForeground(attrJavadoc, new Color(0, 0, 139));  // 文字の色
		SimpleAttributeSet attrComment = new SimpleAttributeSet();
        StyleConstants.setForeground(attrComment, new Color(0, 80, 0));  // 文字の色
		
		String source = doc.getText(0, doc.getLength());

		int index = 0;
		
		Matcher m = null;
		m = PTN_KEYWORD.matcher(source);
		while (m.find(index)) {
			 doc.setCharacterAttributes(m.start(2), m.end(2)-m.start(2), attrKeyword, true);
			 index = m.start(3);
		}
		m = PNT_COMMENT.matcher(source);
		while (m.find()) {
			 doc.setCharacterAttributes(m.start(), m.end()-m.start(), attrComment, true);
		}
		m = PNT_JAVADOC.matcher(source);
		while (m.find()) {
			 doc.setCharacterAttributes(m.start(), m.end()-m.start(), attrJavadoc, true);
		}
	}
}
