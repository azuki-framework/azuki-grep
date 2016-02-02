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

public class GrepTextEditer extends TextEditor {

	/** serialVersionUID */
	private static final long serialVersionUID = -6579545022433764474L;

	public GrepTextEditer() {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			Font font = new Font("ＭＳ ゴシック", Font.PLAIN, 13);
			setFont(font);
		} else {
			Font font = new Font(Font.MONOSPACED, Font.PLAIN, 13);
			setFont(font);
		}

	}
	
	public void addHighlighter(final List<? extends DocumentPosition> positions) {
		HighlightPainter pointer = new DefaultHighlightPainter(Color.yellow);
		try {
			Highlighter highlighter = getHighlighter();
			highlighter.removeAllHighlights();
			for (DocumentPosition pos : positions) {
				highlighter.addHighlight(pos.getStart(), pos.getEnd(), pointer);
			}
		} catch (BadLocationException e) {
		  e.printStackTrace();
		}
	}
}
