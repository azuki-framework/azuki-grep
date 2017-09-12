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
 * @author Kawakicchi
 */
public class GrepCondition {

	private final List<ContainingText> containingTexts;

	private final List<FileNamePattern> fileNamePatterns;

	private final List<TargetDirectory> targetDirectorys;

	private final List<FileNamePattern> excludeFileNamePatterns;

	private final List<DirectoryNamePattern> excludeDirectoryNamePatterns;

	/**
	 * コンストラクタ
	 */
	public GrepCondition() {
		containingTexts = new ArrayList<ContainingText>();
		fileNamePatterns = new ArrayList<FileNamePattern>();
		targetDirectorys = new ArrayList<TargetDirectory>();

		excludeFileNamePatterns = new ArrayList<FileNamePattern>();
		excludeDirectoryNamePatterns = new ArrayList<DirectoryNamePattern>();
	}

	public void addContainingText(final ContainingText containingText) {
		this.containingTexts.add(containingText);
	}

	public void setContainingTexts(final List<ContainingText> containingTexts) {
		this.containingTexts.clear();
		if (null != containingTexts) {
			this.containingTexts.addAll(containingTexts);
		}
	}

	@XmlElementWrapper(name = "ContainingTexts")
	@XmlElement(name = "ContainingText")
	public List<ContainingText> getContainingTexts() {
		return containingTexts;
	}

	public void addFileNamePattern(final FileNamePattern fileNamePattern) {
		this.fileNamePatterns.add(fileNamePattern);
	}

	public void setFileNamePatterns(final List<FileNamePattern> fileNamePatterns) {
		this.fileNamePatterns.clear();
		if (null != fileNamePatterns) {
			this.fileNamePatterns.addAll(fileNamePatterns);
		}
	}

	@XmlElementWrapper(name = "FileNamePatterns")
	@XmlElement(name = "FileNamePattern")
	public List<FileNamePattern> getFileNamePatterns() {
		return fileNamePatterns;
	}

	public void addTargetDirectory(final TargetDirectory targetDirectory) {
		this.targetDirectorys.add(targetDirectory);
	}

	public void setTargetDirectorys(final List<TargetDirectory> targetDirectorys) {
		this.targetDirectorys.clear();
		if (null != targetDirectorys) {
			this.targetDirectorys.addAll(targetDirectorys);
		}
	}

	@XmlElementWrapper(name = "TargetDirectorys")
	@XmlElement(name = "TargetDirectory")
	public List<TargetDirectory> getTargetDirectorys() {
		return targetDirectorys;
	}

	public void addExcludeFileNamePattern(final FileNamePattern fileNamePattern) {
		this.excludeFileNamePatterns.add(fileNamePattern);
	}

	public void setExcludeFileNamePatterns(final List<FileNamePattern> fileNamePatterns) {
		this.excludeFileNamePatterns.clear();
		if (null != fileNamePatterns) {
			this.excludeFileNamePatterns.addAll(fileNamePatterns);
		}
	}

	@XmlElementWrapper(name = "ExcludeFileNamePatterns")
	@XmlElement(name = "ExcludeFileNamePattern")
	public List<FileNamePattern> getExcludeFileNamePatterns() {
		return excludeFileNamePatterns;
	}

	public void addExcludeDirectoryNamePattern(final DirectoryNamePattern directoryNamePattern) {
		this.excludeDirectoryNamePatterns.add(directoryNamePattern);
	}

	public void setExcludeDirectoryNamePatterns(final List<DirectoryNamePattern> directoryNamePatterns) {
		this.excludeDirectoryNamePatterns.clear();
		if (null != directoryNamePatterns) {
			this.excludeDirectoryNamePatterns.addAll(directoryNamePatterns);
		}
	}

	@XmlElementWrapper(name = "ExcludeDirectoryNamePatterns")
	@XmlElement(name = "ExcludeDirectoryNamePattern")
	public List<DirectoryNamePattern> getExcludeDirectoryNamePatterns() {
		return excludeDirectoryNamePatterns;
	}

	public List<File> getTargetDirectoryFiles() {
		List<File> files = new ArrayList<File>();
		for (TargetDirectory directory : targetDirectorys) {
			File file = new File(directory.getValue());
			files.add(new File(file.getAbsolutePath()));
		}
		return files;
	}
}
