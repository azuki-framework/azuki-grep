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
import java.awt.Image;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.azkfw.component.text.NoWrapEditorKit;
import org.azkfw.grep.entity.GrepMatchWord;
import org.azkfw.grep.gui.FileTree.MatchFileObject;
import org.azkfw.grep.gui.FileTree.MatchLineObject;

/**
 * @author Kawakicchi
 *
 */
public class FindFileTreeCell  extends JPanel{

	/** serialVersionUID */
	private static final long serialVersionUID = 433998392285167422L;

	private Pattern PTN_LINE = Pattern.compile("^[0-9]+:");
	private Pattern PTN_MATCH = Pattern.compile("\\([0-9]+ matches\\)");
	
	private ImageIcon icon;
	private JLabel lblIcon;
	private JTextPane txtTitle;
	
	private Color backgroundSelectionColor = new Color(220, 240, 255);
	
	private MutableAttributeSet atrDefault;
	private MutableAttributeSet atrMatch;
	private MutableAttributeSet atrLine;
	private MutableAttributeSet atrMatchWord;
	
	private int iconSize = 18;

	public FindFileTreeCell(final Object value) {
		setLayout(null);
		
		String text = value.toString();

		Font font;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			font = new Font("ＭＳ ゴシック", Font.PLAIN, 12);
		} else {
			font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
		}

		atrDefault = new SimpleAttributeSet();
		StyleConstants.setForeground(atrDefault, new Color(20, 20, 20));
		atrMatch = new SimpleAttributeSet();
		StyleConstants.setForeground(atrMatch, new Color(40,40,200));
		atrLine = new SimpleAttributeSet();
		StyleConstants.setForeground(atrLine, new Color(128, 128, 128));
		atrMatchWord = new SimpleAttributeSet();
		StyleConstants.setForeground(atrMatchWord, new Color(255, 0, 0));

		icon = new ImageIcon();
		lblIcon = new JLabel(icon);
		lblIcon.setLocation(0, 0);
		lblIcon.setSize(iconSize, iconSize);
		lblIcon.setBackground(Color.blue);
		add(lblIcon);

		txtTitle = new JTextPane();
		txtTitle.setFont(font);
		txtTitle.setEditorKit(new NoWrapEditorKit());
		//txtTitle.setBackground(Color.red);
		txtTitle.setOpaque(false);
		txtTitle.setBorder(BorderFactory.createEmptyBorder());
		txtTitle.setForeground(Color.BLACK);
		//txtTitle.setBackground(Color.WHITE);
		txtTitle.setEditable(false);
		add(txtTitle);

		txtTitle.setText(text);

		int length = txtTitle.getDocument().getLength();
		txtTitle.getStyledDocument().setCharacterAttributes(0, length, atrDefault, true);
		
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object obj = node.getUserObject();
			if (obj instanceof MatchFileObject) {
				// File
				Matcher m = PTN_MATCH.matcher(txtTitle.getText());
				if (m.find()) {
					txtTitle.getStyledDocument().setCharacterAttributes(m.start(), m.end(), atrMatch, true);
				}
			} else if (obj instanceof MatchLineObject) {
				// line 
				Matcher m = PTN_LINE.matcher(txtTitle.getText());
				if (m.find()) {
					txtTitle.getStyledDocument().setCharacterAttributes(m.start(), m.end(), atrLine, true);
				}
				
				try {
				int index = txtTitle.getText().indexOf(":") + 2;
				GrepMatchWord word = ((MatchLineObject)obj).getMatchWord();
				int start = word.getStart() - word.getLineStart() + index;
				int end = start + (word.getEnd()-word.getStart()) + index;
				
				HighlightPainter pointer = new DefaultHighlightPainter(Color.yellow);
				txtTitle.getHighlighter().addHighlight(start, end, pointer);
				//txtTitle.getStyledDocument().setCharacterAttributes(start, (word.getEnd()-word.getStart()), atrMatchWord, true);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		}

		FontMetrics fm = txtTitle.getFontMetrics(txtTitle.getFont());
		int width = fm.stringWidth(text);
		//System.out.println(value.toString() + " " + width);

		lblIcon.setLocation(0, 0);
		lblIcon.setSize(iconSize, iconSize);
		
		txtTitle.setLocation(iconSize,  0);

		Dimension dm1 = new Dimension(width, iconSize);
		txtTitle.setSize(dm1);
		txtTitle.setPreferredSize(dm1);

		Dimension dm2 = new Dimension(width + iconSize, iconSize);
		setSize(dm2);
		setPreferredSize(dm2);
	}
	
	public void setImage(final Image image) {
		icon.setImage(image);
	}
	
	public void setSelected(final boolean isSelected) {
		setBackground(isSelected ? backgroundSelectionColor : Color.WHITE);
	}
}
