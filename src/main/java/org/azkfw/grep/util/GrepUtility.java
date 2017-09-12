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
package org.azkfw.grep.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Kawakicchi
 *
 */
public class GrepUtility {

	private GrepUtility() {

	}

	public static boolean isNull(final Object object) {
		return (null == object);
	}

	public static boolean isNotNull(final Object object) {
		return (!isNull(object));
	}

	public static boolean isEmpty(final List<?> list) {
		return (null == list || 0 == list.size());
	}

	public static boolean isEmpty(final String string) {
		return (null == string || 0 == string.length());
	}

	public static void release(final InputStream stream) {
		if (null != stream) {
			try {
				stream.close();
			} catch (IOException ex) {

			}
		}
	}
}
