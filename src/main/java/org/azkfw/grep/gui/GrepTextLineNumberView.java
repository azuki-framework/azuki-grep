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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

public class GrepTextLineNumberView extends JComponent {

	/** serialVersionUID */
	private static final long serialVersionUID = 5766991295487339962L;

	private static final int MARGIN = 5;

	private final JTextPane textArea;
	private final JScrollBar scrollBar;

	private final FontMetrics fontMetrics;
	private final Insets textInsets;

	// private final int topInset;
	private final int fontAscent;
	private final int fontHeight;
	private final int fontDescent;
	private final int fontLeading;

	private final int fontWidth;
	
	private final Color THUMB_COLOR = new Color(0, 0, 255, 50);
	  private final Rectangle thumbRect = new Rectangle();

	public GrepTextLineNumberView(final JTextPane textArea, final JScrollBar scrollBar) {
		this.textArea = textArea;
		this.scrollBar = scrollBar;

		textInsets = textArea.getInsets();
		Font font = textArea.getFont();

		fontMetrics = getFontMetrics(font);
		fontHeight = fontMetrics.getHeight();
		fontAscent = fontMetrics.getAscent();
		fontDescent = fontMetrics.getDescent();
		fontLeading = fontMetrics.getLeading();
		// topInset = textArea.getInsets().top;

		fontWidth = fontMetrics.charWidth('m');

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				repaint();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				repaint();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		textArea.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				revalidate();
				repaint();
			}
		});

		Insets i = textArea.getInsets();
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
				BorderFactory.createEmptyBorder(i.top, MARGIN, i.bottom, MARGIN - 1)));
		setOpaque(true);
		setBackground(Color.WHITE);
		setFont(font);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getComponentWidth(), textArea.getHeight());
	}

	@Override
	public void paintComponent(final Graphics g) {
		g.setColor(getBackground());
		Rectangle clip = g.getClipBounds();
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		// System.out.println(String.format("clip %d %d %d %d", clip.x, clip.y, clip.width, clip.height));

		int x2 = clip.x;
		int y2 = clip.y;
		
		int itop = scrollBar.getInsets().top;
		BoundedRangeModel range = scrollBar.getModel();
		double sy = range.getExtent() / (double) (range.getMaximum() - range.getMinimum());
		AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
		Highlighter highlighter = textArea.getHighlighter();
		    

	    if (scrollBar.isVisible()) {
		      thumbRect.height = range.getExtent();
		      thumbRect.y = range.getValue();
		      Rectangle s = at.createTransformedShape(thumbRect).getBounds();
		      //System.out.println(String.format("%d %d", s.y, s.height));
		      g.setColor(THUMB_COLOR);
		      g.fillRect(x2, y2 + itop + s.y, 8, s.height);
		 }

		// -----
	    //paint Highlight
	    g.setColor(Color.BLUE);
	    try {
	      for (Highlighter.Highlight hh: highlighter.getHighlights()) {
	        Rectangle r = textArea.modelToView(hh.getStartOffset());
	        Rectangle s = at.createTransformedShape(r).getBounds();
	        int h = Math.max(1, (int)(Math.ceil((double)(fontAscent + fontDescent + fontLeading) * sy)));
	        g.fillRect(x2, y2 + itop + s.y, 8, h);
	      }
	    } catch (BadLocationException e) {
	      e.printStackTrace();
	    }
		
		
		g.setColor(getForeground());
		int base = clip.y;
		int start = getLineAtPoint(base);
		int end = getLineAtPoint(base + clip.height);
		int y = start * fontHeight + textInsets.top;
		int rmg = getBorder().getBorderInsets(this).right;
		for (int i = start; i <= end; i++) {
			String text = String.valueOf(i + 1);
			int x = getComponentWidth() - rmg - fontMetrics.stringWidth(text);
			y += fontAscent;
			g.drawString(text, x, y);
			y += fontDescent + fontLeading;
		}
	}

	private int getComponentWidth() {
		Document doc = textArea.getDocument();
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementIndex(doc.getLength()) + 1;
		int maxDigits = Math.max(2, String.valueOf(lineCount).length());
		Insets i = getBorder().getBorderInsets(this);
		int width = maxDigits * fontWidth + i.left + i.right;
		return width + 8;
	}

	private int getLineAtPoint(int y) {
		Element root = textArea.getDocument().getDefaultRootElement();
		int pos = textArea.viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}
}