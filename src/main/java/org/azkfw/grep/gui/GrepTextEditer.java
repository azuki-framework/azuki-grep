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
package org.azkfw.grep.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

import org.azkfw.component.text.TextEditor;
import org.azkfw.grep.entity.GrepMatchWord;

public class GrepTextEditer extends TextEditor {

	/** serialVersionUID */
	private static final long serialVersionUID = -6579545022433764474L;

	public GrepTextEditer() {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			Font font = new Font("ＭＳ ゴシック", Font.PLAIN, 14);
			setFont(font);
		} else {
			Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
			setFont(font);
		}

		setEditable(false);
	}

	public void clearHighlighter() {
		Highlighter highlighter = getHighlighter();
		highlighter.removeAllHighlights();
	}

	public void addHighlighter(final List<GrepMatchWord> words) {
		HighlightPainter pointer = new DefaultHighlightPainter(Color.yellow);
		Highlighter highlighter = getHighlighter();
		try {
			for (GrepMatchWord word : words) {
				highlighter.addHighlight(word.getVirtualStart(), word.getVirtualEnd(), pointer);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void addMark(final String string) {
		addMark(string, Color.cyan);
	}

	public void addMark(final String string, final Color color) {
		Pattern ptn = Pattern.compile(string);

		HighlightPainter pointer = new DefaultHighlightPainter(color);
		Highlighter highlighter = getHighlighter();
		try {
			String s = getDocument().getText(0, getDocument().getLength());
			Matcher m = ptn.matcher(s);
			while (m.find()) {
				highlighter.addHighlight(m.start(), m.end(), pointer);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public class VisibleCaret extends DefaultCaret {
		private static final long serialVersionUID = 1140099288992101139L;

		public VisibleCaret() {
		}

		public VisibleCaret(int rate) {
			setBlinkRate(rate);
		}

		@Override
		protected synchronized void damage(Rectangle r) {
			if (r != null) {
				JTextComponent c = getComponent();
				x = 0;
				y = r.y;
				width = c.getSize().width;
				height = r.height;
				c.repaint();
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
			setVisible(true);
			setSelectionVisible(true);
		}
	}
}
