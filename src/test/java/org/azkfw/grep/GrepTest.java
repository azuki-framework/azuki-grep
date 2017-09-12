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

import junit.framework.TestCase;

import org.azkfw.grep.cash.CashStore;
import org.azkfw.grep.entity.ContainingText;
import org.azkfw.grep.entity.FileNamePattern;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.TargetDirectory;
import org.junit.Test;

/**
 * このクラスは、{@link Grep} の評価を行うテストクラスです。
 * 
 * @author Kawakicchi
 */
public class GrepTest extends TestCase {

	@Test
	public void test() {
		final GrepCondition condition = new GrepCondition();
		condition.addContainingText(new ContainingText("List"));
		condition.addTargetDirectory(new TargetDirectory("../"));
		condition.addFileNamePattern(new FileNamePattern("*.java"));
		condition.addFileNamePattern(new FileNamePattern("*.xml"));
		condition.addFileNamePattern(new FileNamePattern("*.txt"));

		CashStore store = new CashStore();
		final Grep grep = new Grep(store);

		final long tmStart = System.nanoTime();

		for (int i = 0; i < 5; i++) {
			grep.start(condition);
			grep.waitFor();
		}

		final long tmEnd = System.nanoTime();

		System.out.println(String.format("%fsec", (((double) tmEnd - tmStart) / 1000000000f)));
	}
}
