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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Kawakicchi
 *
 */
public class GrepCondition {
	
	public static void main(final String[] args) {
		GrepCondition condition = new GrepCondition();
		condition.setFileNamePatterns("*.java , ?.java , !x");
	}

	private static final Pattern PTN_FILE_NAME_SPLIT = Pattern.compile("[\\s]*,[\\s]*");

	private String containingText;
	
	private File targetDirectory;
	
	private List<Pattern> fileNamePatterns;
	
	public GrepCondition() {
		fileNamePatterns = new ArrayList<Pattern>();
	}
	
	public void setTargetDirectory(final File directory) {
		targetDirectory = directory;
	}
	
	public void setTargetDirectory(final String path) {
		targetDirectory = new File(path);
	}
	
	public File getTargetDirectory() {
		return targetDirectory;
	}
	
	/**
	 * comma
	 * @param patterns
	 */
	public void setFileNamePatterns(final String patterns) {
		setFileNamePatterns(patterns, Pattern.CASE_INSENSITIVE);
	}
	
	/**
	 * comma
	 * @param patterns
	 */
	public void setFileNamePatterns(final String patterns, final int flags) {
		fileNamePatterns.clear();
		String[] split = patterns.split("[\\s]*,[\\s]*");
		for (String str : split) {
			// System.out.println(str);
			String ptn = str.replaceAll("\\.", "\\\\.");
			ptn = ptn.replaceAll("\\*", ".*");
			ptn = ptn.replaceAll("\\?", ".+");
			ptn = ptn.replaceAll("\\!", "^");
			// System.out.println(ptn);
			fileNamePatterns.add( Pattern.compile(ptn, flags) );
		}
	}
	
	public List<Pattern> getFileNamePatterns() {
		return fileNamePatterns;
	}	
	
	public void setContainingText(final String text) {
		containingText = text;
	}
	public String getContainingText() {
		return containingText;
	}
}
