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
package org.azkfw.grep.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * 
 * @author Kawakicchi
 */
public class GrepCondition {
	
	private List<ContainingText> containingTexts;

	private String fileNamePatternsPlain;
	private List<Pattern> fileNamePatterns;
	
	private String targetDirectorysPlain;
	private List<File> targetDirectorys;
	
	/**
	 * コンストラクタ
	 */
	public GrepCondition() {
		containingTexts = new ArrayList<ContainingText>();
		fileNamePatternsPlain = null;
		fileNamePatterns = new ArrayList<Pattern>();
		targetDirectorys = new ArrayList<File>();
	}
	
	public void setContainingTexts(final List<ContainingText> containingTexts) {
    	this.containingTexts = containingTexts;
    }

    @XmlElementWrapper(name="ContainingTexts")
    @XmlElement(name="ContainingText")
	public List<ContainingText> getContainingTexts() {
		return containingTexts;
	}
    
	/**
	 * comma
	 * @param patterns
	 */
	public void setFileNamePatterns(final String patterns) {
    	System.out.println("setFileNamePatterns:"+patterns);
		setFileNamePatterns(patterns, Pattern.CASE_INSENSITIVE);
	}
	
	/**
	 * comma
	 * @param patterns
	 */
	public void setFileNamePatterns(final String patterns, final int flags) {
		fileNamePatternsPlain = patterns;
		fileNamePatterns.clear();
		String[] split = patterns.split("[\\s]*,[\\s]*");
		for (String str : split) {
			String ptn = str.replaceAll("\\.", "\\\\.");
			ptn = ptn.replaceAll("\\*", ".*");
			ptn = ptn.replaceAll("\\?", ".+");
			ptn = ptn.replaceAll("\\!", "^");
			fileNamePatterns.add( Pattern.compile(ptn, flags) );
		}
	}
	
	public List<Pattern> getFileNamePatterns() {
		return fileNamePatterns;
	}
    
    @XmlElement(name="FileNamePatterns")
    public String getFileNamePatternsToString() {
    	return fileNamePatternsPlain;
    }
	
	/**
	 * Grep対象のディレクトリを複数設定する。
	 * @param directorys ディレクトリ（;区切り)
	 */
	public void setTargetDirectorys(final String directorys) {
    	System.out.println("setTargetDirectorys:"+directorys);
		targetDirectorysPlain = directorys;
		targetDirectorys.clear();
		String[] split = directorys.split("[\\s]*;[\\s]*");
		for (String str : split) {
			File file = new File(str);
			targetDirectorys.add( new File(file.getAbsolutePath()) );
		}
	}
	
	/**
	 * Grep対象のディレクトリを取得する。
	 * @return
	 */
	public List<File> getTargetDirectorys() {
		return targetDirectorys;
	}

    @XmlElement(name="TargetDirectorys")
    public String getTargetDirectorysToString() {
    	return targetDirectorysPlain;
    }
}
