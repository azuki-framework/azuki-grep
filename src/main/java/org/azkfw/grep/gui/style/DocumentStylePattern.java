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

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;

/**
 * @author Kawakicchi
 */
public class DocumentStylePattern {

	private final Pattern pattern;

	private final int group;

	private final AttributeSet attributeSet;

	private final boolean replace;

	public DocumentStylePattern(final Pattern pattern, final int group, final AttributeSet attributeSet) {
		this.pattern = pattern;
		this.group = group;
		this.attributeSet = attributeSet;
		this.replace = false;
	}

	public DocumentStylePattern(final Pattern pattern, final int group, final AttributeSet attributeSet, final boolean replace) {
		this.pattern = pattern;
		this.group = group;
		this.attributeSet = attributeSet;
		this.replace = replace;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public int getGroup() {
		return group;
	}

	public AttributeSet getAttributeSet() {
		return attributeSet;
	}

	public boolean isReplace() {
		return replace;
	}
}
