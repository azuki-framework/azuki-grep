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
package org.azkfw.grep.cash;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Kawakicchi
 */
public class CashStore {

	private final Map<String, CashFile> mapFiles;

	public CashStore() {
		mapFiles = new HashMap<String, CashFile>();
	}

	public boolean push(final CashFile file) {
		boolean result = false;

		final String path = file.getFile().getAbsolutePath();

		synchronized (this) {
			mapFiles.put(path, file);

		}

		return result;
	}

	public CashFile getFile(final File file) {
		CashFile result = null;

		final String path = file.getAbsolutePath();

		synchronized (this) {
			result = mapFiles.get(path);
		}

		return result;
	}

}
