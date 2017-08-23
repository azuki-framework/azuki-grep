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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kawakicchi
 */
public final class DocumentStyleFactory {

	private static final DocumentStyleFactory DEFAULT = new DocumentStyleFactory();

	private final List<DocumentStyle> styles;

	private DocumentStyleFactory() {
		styles = new ArrayList<DocumentStyle>();
		styles.add(new CDocumentStyle());
		styles.add(new JavaDocumentStyle());
		styles.add(new SQLDocumentStyle());
		styles.add(new DSQLDocumentStyle());
	}

	public static DocumentStyleFactory getDefaultInstance() {
		return DEFAULT;
	}

	public DocumentStyle getSupportDocumentStyle(final File file) {
		DocumentStyle documentStyle = null;
		for (DocumentStyle style : styles) {
			if (style.isSupport(file)) {
				documentStyle = style;
				break;
			}
		}
		return documentStyle;
	}
}
