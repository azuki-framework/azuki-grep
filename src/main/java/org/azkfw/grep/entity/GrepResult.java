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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Kawakicchi
 *
 */
public class GrepResult {

	private long processingNanoTime;
	
	private List<GrepMatchFile> matchFiles;
	
	public GrepResult() {
		processingNanoTime = -1;
		matchFiles = null;
	}
	
	public void setProcessingNanoTime(final long nanoTime) {
		processingNanoTime = nanoTime;
	}
	
	public long getProcessingNanoTime() {
		return processingNanoTime;
	}
	
	public void setMatchFiles(final List<GrepMatchFile> files) {
		matchFiles = files;
	}
	
	@XmlElementWrapper(name="GrepMatchFiles")
	@XmlElement(name="GrepMatchFile")
	public List<GrepMatchFile> getMatchFiles() {
		return matchFiles;
	}
	
}
