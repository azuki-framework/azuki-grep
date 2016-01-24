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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 
 * @author Kawakicchi
 */
public class CashStore {

	private Queue<CashFile> queueFiles;
	private Map<String, CashFile> mapFiles;

	private long maxCashSize;
	private long nowCashSize;

	public CashStore() {
		queueFiles = new LinkedList<CashFile>();
		mapFiles = new HashMap<String, CashFile>();

		maxCashSize = 100 * 1024 * 1024;
		nowCashSize = 0;
	}

	public boolean push(final CashFile file) {
		boolean result = false;
		synchronized (queueFiles) {
			String path = file.getFile().getAbsolutePath();
			if (mapFiles.containsKey(path)) {
				CashFile cf = mapFiles.get(path);
				nowCashSize -= cf.getLength();
				queueFiles.remove(cf);
				mapFiles.remove(path);
			}

			while (maxCashSize < nowCashSize + file.getLength()) {
				CashFile cf = queueFiles.poll();
				if (null == cf) {
					break;
				}
				nowCashSize -= cf.getLength();
				mapFiles.remove(cf.getFile().getAbsolutePath());
			}

			if (maxCashSize >= nowCashSize + file.getLength()) {
				mapFiles.put(path, file);
				queueFiles.offer(file);
				nowCashSize += file.getLength();
				result = true;
			}
		}
		return result;
	}

	public CashFile getFile(final File file) {
		CashFile result = null;
		String path = file.getAbsolutePath();
		synchronized (queueFiles) {
			if (mapFiles.containsKey(path)) {
				result = mapFiles.get(path);
				queueFiles.remove(result);
				queueFiles.offer(result);
			}
		}
		return result;
	}

}
