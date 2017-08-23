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
import java.util.regex.Pattern;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author Kawakicchi
 */
public class DSQLDocumentStyle extends SQLDocumentStyle {

	private static final Pattern PTN_FILE = Pattern.compile("^.*\\.(dsql)$", Pattern.CASE_INSENSITIVE);

	public DSQLDocumentStyle() {
		final Pattern PTN_BIND = Pattern.compile("(^|[\\r\\n])(\\$\\{[^\\}]*\\})");

		final Pattern PTN_COMMENT2 = Pattern.compile("(^|[\\r\\n])(#[^\\r\\n]*)");

		final SimpleAttributeSet ATTR_BIND = new SimpleAttributeSet();
		StyleConstants.setBold(ATTR_BIND, true);
		StyleConstants.setForeground(ATTR_BIND, new Color(255, 0, 0));

		final SimpleAttributeSet ATTR_COMMENT = new SimpleAttributeSet();
		StyleConstants.setForeground(ATTR_COMMENT, new Color(63, 127, 95));

		addDocumentStylePattern(new DocumentStylePattern(PTN_BIND, 2, ATTR_BIND, true));

		addDocumentStylePattern(new DocumentStylePattern(PTN_COMMENT2, 2, ATTR_COMMENT, true));
	}

	public boolean isSupport(final File file) {
		return PTN_FILE.matcher(file.getName()).matches();
	}
}
