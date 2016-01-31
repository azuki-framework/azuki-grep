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

import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.azkfw.grep.entity.GrepMatchFile;

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
	
	public FileTree() {		
		setRowHeight(18);
		setRootVisible(false);
		
		FindFileTreeCellRenderer renderer = new FindFileTreeCellRenderer();
		URL urlFile = this.getClass().getResource("/org/azkfw/grep/gui/file.png");
		URL urlFolder = this.getClass().getResource("/org/azkfw/grep/gui/folder.png");
		try {
			renderer.setFileImage( createImage((ImageProducer)urlFile.getContent()) );
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			renderer.setFolderImage( createImage((ImageProducer)urlFolder.getContent()) );
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		//setCellRenderer(new HighlightTreeCellRenderer());
		setCellRenderer(renderer);
		
		root = new DefaultMutableTreeNode("");
		
		model = new DefaultTreeModel(root);
		setModel(model);
	}

	public void setRootDirectorys(final List<File> dirs) {
		root = new DefaultMutableTreeNode("");
		model.setRoot(root);
		rootPath = new TreePath(root);
		
		for (File dir : dirs) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getAbsolutePath());
			root.add(node);
		}
	}

	public void addFile(final GrepMatchFile file) {
		String path = file.getFile().getAbsolutePath();
		
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) root.getChildAt(i);
			String path2 = (String) node2.getUserObject();
			if ( path.startsWith(path2) ) {
				path = path.substring(path2.length()) ;
				if (path.startsWith(toString(File.separatorChar))) {
					path = path.substring(1);
				}

				String[] split = path.split(toString(File.separatorChar));
				
				add(node2, rootPath.pathByAddingChild(node2), 0, split, file);
				break;
			}
		}

	}

	private void add(final DefaultMutableTreeNode parent, final TreePath path, final int index, final String[] strings, final GrepMatchFile file) {
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
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(new MatchFileObject(file));
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
	
	public static class MatchFileObject {
		
		private GrepMatchFile file;

		public MatchFileObject(final GrepMatchFile file) {
			this.file = file;
		}
		
		public GrepMatchFile getMatchFile() {
			return file;
		}
				
		public String toString() {
			String s = String.format("%s [%s] (%d matches)", file.getFile().getName(), file.getCharset(), file.getWords().size());
			return s;
		}
	}
}
