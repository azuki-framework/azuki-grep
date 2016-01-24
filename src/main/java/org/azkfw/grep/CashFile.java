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

/**
 * 
 * @author Kawakicchi
 */
public class CashFile {

	private File file;

	private String data;
	
	private long length;
	
	private String charset;
	
	public CashFile(final File file, final String data, final String charset) {
		this.file = file;
		this.data = data;
		this.charset = charset;
		this.length = file.length();
	}
	
	public File getFile() {
		return file;
	}
	
	public String getSource() {
		return data;
	}
	
	public long getLength() {
		return length;
	}
	
	public String getCharset() {
		return charset;
	}
}
