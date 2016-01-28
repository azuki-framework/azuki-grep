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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.azkfw.grep.FindFile;

/**
 * 
 * @author Kawakicchi
 */
public class FileTree extends JTree {

	/** serialVersionUID */
	private static final long serialVersionUID = -8543102384439670531L;

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private TreePath rootPath;

	private File rootDirectory;
	
	private Image imgFile;
	private Image imgFolder;

	public FileTree() {
		URL urlFile = this.getClass().getResource("/org/azkfw/grep/gui/file.png");
		URL urlFolder = this.getClass().getResource("/org/azkfw/grep/gui/folder.png");
		try {
		imgFile = createImage((ImageProducer)urlFile.getContent());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
		imgFolder = createImage((ImageProducer)urlFolder.getContent());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		setRowHeight(18);
		
		//setCellRenderer(new HighlightTreeCellRenderer());
		setCellRenderer(new FindFileTreeCellRenderer());
		
		root = new DefaultMutableTreeNode("");
		
		model = new DefaultTreeModel(root);
		setModel(model);
	}

	public void setRootDirectory(final File dir) {
		rootDirectory = dir;
		
		root = new DefaultMutableTreeNode(dir.getAbsolutePath());
		
		model.setRoot(root);
		rootPath = new TreePath(root);
	}

	public void addFile(final FindFile file) {
		String path = file.getFile().getAbsolutePath();
		path = path.substring(rootDirectory.getAbsolutePath().length());
		if (path.startsWith(toString(File.separatorChar))) {
			path = path.substring(1);
		}

		String[] split = path.split(toString(File.separatorChar));
		
		add(root, rootPath, 0, split, file);
	}

	private void add(final DefaultMutableTreeNode parent, final TreePath path, final int index, final String[] strings, final FindFile file) {
		String name = strings[index];

		DefaultMutableTreeNode nextParent = null;

		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
			Object obj = node.getUserObject();
			if (obj instanceof String) {
				String str = (String) obj;
				if (name.equals(str)) {
					nextParent = node;
					break;
				}
			}
		}
		
		if (null == nextParent) {
			if (strings.length == index + 1) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FindFileObject(file));
				parent.add(node);
				nextParent = node;
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				parent.add(node);
				nextParent = node;
			}
			model.reload(parent);
		}

		if (strings.length > index + 1) {
			add(nextParent, path.pathByAddingChild(nextParent), index + 1, strings, file);
			// expandPath(path.pathByAddingChild(nextParent));
		}
	}

	private String toString(final char c) {
		if ('\\' == c) {
			return "\\\\";
		} else {
			return Character.toString(c);
		}
	}

	public void expandAll() {
		expand(root, rootPath);
	}

	private void expand(final TreeNode parent, final TreePath path) {
		expandPath(path);
		for (int i = 0; i < parent.getChildCount(); i++) {
			TreeNode node = parent.getChildAt(i);
			expand(node, path.pathByAddingChild(node));
		}
	}
	
	private class FindFileTreeCellRenderer implements TreeCellRenderer{

		@Override
		public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded,
				final boolean leaf, final int row, final boolean hasFocus) {
			
			FindFileTreeCell cell = new FindFileTreeCell(value);

			cell.setSelected(isSelected);

			return cell;
		}
	}
	
	private class FindFileTreeCell extends JPanel{

		/** serialVersionUID */
		private static final long serialVersionUID = 433998392285167422L;

		private Pattern PTN_MATCH = Pattern.compile("\\([0-9]+ matches\\)");
		
		private ImageIcon icon;
		private JLabel lblIcon;
		private JTextPane txtTitle;
		
		private Color backgroundSelectionColor = new Color(220, 240, 255);
		
		private MutableAttributeSet atrDefault;
		private MutableAttributeSet atrMatch;
		
		private int iconSize = 20;

		public FindFileTreeCell(final Object value) {
			setLayout(null);
		
			atrDefault = new SimpleAttributeSet();
			StyleConstants.setForeground(atrDefault, new Color(20, 20, 20));
			atrMatch = new SimpleAttributeSet();
			StyleConstants.setForeground(atrMatch, new Color(40,40,200));

			icon = new ImageIcon();
			lblIcon = new JLabel(icon);
			lblIcon.setLocation(0, 0);
			lblIcon.setSize(iconSize, iconSize);
			lblIcon.setBackground(Color.blue);
			add(lblIcon);
			
			txtTitle = new JTextPane();
			//txtTitle.setBackground(Color.red);

			txtTitle.setBounds(iconSize, 0, 200, 20);

			txtTitle.setOpaque(false);
			txtTitle.setBorder(BorderFactory.createEmptyBorder());
			txtTitle.setForeground(Color.BLACK);
			//txtTitle.setBackground(Color.WHITE);
			txtTitle.setEditable(false);

			add(txtTitle);
			
			setPreferredSize(new Dimension(iconSize + 200, 20));
			
			/*
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					Insets insets = getInsets();
					int width = getWidth() - (insets.left + insets.right);
					int height = getHeight() - (insets.top + insets.bottom);
					txtTitle.setLocation(iconSize, 0);
					txtTitle.setSize(width-iconSize, height);
				}
			});
			*/
			
			//Insets insets = txtTitle.getInsets();
			txtTitle.setText(value.toString());
			int length = txtTitle.getDocument().getLength();

			txtTitle.getStyledDocument().setCharacterAttributes(0, length, atrDefault, true);
			
			if (value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				Object obj = node.getUserObject();
				if (obj instanceof FindFileObject) {
					// File
					Matcher m = PTN_MATCH.matcher(txtTitle.getText());
					if (m.find()) {
						txtTitle.getStyledDocument().setCharacterAttributes(m.start(), m.end(), atrMatch, true);
					}
					icon.setImage(imgFile);
				} else if (obj instanceof String) {
					// String
					icon.setImage(imgFolder);
				}
			}

			FontMetrics fm = txtTitle.getFontMetrics(txtTitle.getFont());
			int width = fm.stringWidth(value.toString());
			System.out.println(value.toString() + " " + width);
			
			lblIcon.setLocation(0, 0);
			lblIcon.setSize(iconSize, iconSize);
			
			txtTitle.setLocation(iconSize,  0);
			txtTitle.setSize(width, 20);

			Dimension dm = new Dimension(width + iconSize, 20);
			setSize(dm);
			setPreferredSize(dm);
			
		}
		
		public void setSelected(final boolean isSelected) {
			setBackground(isSelected ? backgroundSelectionColor : Color.WHITE);
		}
	}
		
	public static class FindFileObject {
		private FindFile file;
		public FindFileObject(final FindFile file) {
			this.file = file;
		}
		public FindFile getFindFile() {
			return file;
		}
		public String toString() {
			String s = String.format("%s [%s] (%d matches)", file.getFile().getName(), file.getCharset(), file.getMatchs().size());
			return s;
		}
	}
}
