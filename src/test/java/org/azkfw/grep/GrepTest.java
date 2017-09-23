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
import org.azkfw.grep.entity.GrepStatistics;
import org.azkfw.grep.entity.TargetDirectory;
import org.azkfw.grep.util.FormatUtility;
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

		{
			Grep grep = new Grep();
			for (int i = 0; i < 3; i++) {
				final long tmStart = System.nanoTime();
				grep.start(condition);
				grep.waitFor();
				final long tmEnd = System.nanoTime();
				System.out.println(String.format("Cash OFF [%d] %f sec", i, (((double) tmEnd - tmStart) / 1000000000f)));
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {

		}
		{
			CashStore store = new CashStore();
			Grep grep = new Grep(store);
			for (int i = 0; i < 3; i++) {
				final long tmStart = System.nanoTime();
				grep.start(condition);
				grep.waitFor();
				final long tmEnd = System.nanoTime();
				System.out.println(String.format("Cash ON  [%d] %f sec", i, (((double) tmEnd - tmStart) / 1000000000f)));
			}

			final GrepStatistics statistics = grep.getStatistics();
			System.out.println(String.format("Total file size %d", statistics.getSearchFileCount()));
			System.out.println(String.format("Target file size %d", statistics.getTargetFileCount()));
			System.out.println(String.format("Hit file size %d", statistics.getHitFileCount()));
			System.out.println(String.format("Read size %s", FormatUtility.byteToString(statistics.getTotalTargetFileLength())));
		}
	}
}
