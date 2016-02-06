package org.azkfw.grep.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.azkfw.component.text.TextEditor;
import org.azkfw.grep.entity.DocumentPosition;
import org.azkfw.grep.entity.GrepMatchFile;
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
}
