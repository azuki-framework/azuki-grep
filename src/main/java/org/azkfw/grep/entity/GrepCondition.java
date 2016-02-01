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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * 
 * @author Kawakicchi
 */
public class GrepCondition {
	
	private List<ContainingText> containingTexts;

	private List<FileNamePattern> fileNamePatterns;

	private List<TargetDirectory> targetDirectorys;

	/**
	 * コンストラクタ
	 */
	public GrepCondition() {
		containingTexts = new ArrayList<ContainingText>();
		fileNamePatterns = new ArrayList<FileNamePattern>();
		targetDirectorys = new ArrayList<TargetDirectory>();
	}

	public void setContainingTexts(final List<ContainingText> containingTexts) {
    	this.containingTexts = containingTexts;
    }

	@XmlElementWrapper(name="ContainingTexts")
	@XmlElement(name="ContainingText")
	public List<ContainingText> getContainingTexts() {
		return containingTexts;
	}
    
	public void setFileNamePatterns(final List<FileNamePattern> fileNamePatterns) {
		this.fileNamePatterns = fileNamePatterns;
	}

	@XmlElementWrapper(name="FileNamePatterns")
	@XmlElement(name="FileNamePattern")
	public List<FileNamePattern> getFileNamePatterns() {
		return fileNamePatterns;
	}

	public void setTargetDirectorys(final List<TargetDirectory> targetDirectorys) {
		this.targetDirectorys = targetDirectorys;
	}

	@XmlElementWrapper(name="TargetDirectorys")
	@XmlElement(name="TargetDirectory")
	public List<TargetDirectory> getTargetDirectorys() {
		return targetDirectorys;
	}

	public List<File> getTargetDirectoryFiles() {
		List<File> files = new ArrayList<File>();
		for (TargetDirectory directory : targetDirectorys) {
			File file = new File(directory.getValue());
			files.add(new File(file.getAbsolutePath()));
		}
		return files;
	}

	//	/**
//	 * comma
//	 * @param patterns
//	 */
//	public void setFileNamePatterns(final String patterns, final int flags) {
//		fileNamePatternsPlain = patterns;
//		fileNamePatterns.clear();
//		String[] split = patterns.split("[\\s]*,[\\s]*");
//		for (String str : split) {
//			String ptn = str.replaceAll("\\.", "\\\\.");
//			ptn = ptn.replaceAll("\\*", ".*");
//			ptn = ptn.replaceAll("\\?", ".+");
//			ptn = ptn.replaceAll("\\!", "^");
//			fileNamePatterns.add( Pattern.compile(ptn, flags) );
//		}
//	}

//	/**
//	 * Grep対象のディレクトリを複数設定する。
//	 * @param directorys ディレクトリ（;区切り)
//	 */
//	public void setTargetDirectorys(final String directorys) {
//    	System.out.println("setTargetDirectorys:"+directorys);
//		targetDirectorysPlain = directorys;
//		targetDirectorys.clear();
//		String[] split = directorys.split("[\\s]*;[\\s]*");
//		for (String str : split) {
//			File file = new File(str);
//			targetDirectorys.add( new File(file.getAbsolutePath()) );
//		}
//	}
}
