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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
	
	public FileTree() {		
		setRowHeight(18);
		
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
