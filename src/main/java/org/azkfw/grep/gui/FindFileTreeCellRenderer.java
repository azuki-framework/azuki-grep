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

import java.awt.Component;
import java.awt.Image;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.azkfw.grep.gui.FileTree.MatchFileObject;
import org.azkfw.grep.gui.FileTree.MatchLineObject;

/**
 * @author Kawakicchi
 *
 */
public class FindFileTreeCellRenderer implements TreeCellRenderer{

	private Image imgFile;

	private Image imgFolder;

	private Image imgArrow;

	/**
	 * 
	 */
	public FindFileTreeCellRenderer() {
	}
	
	public void setFileImage(final Image image) {
		imgFile = image;
	}
	
	public void setFolderImage(final Image image) {
		imgFolder = image;
	}
	
	public void setArrowImage(final Image image) {
		imgArrow = image;
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		
		FindFileTreeCell cell = new FindFileTreeCell(value);

		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object obj = node.getUserObject();
			if (obj instanceof MatchFileObject) {
				cell.setImage(imgFile);
			} else if (obj instanceof MatchLineObject) {
				cell.setImage(imgArrow);
			} else if (obj instanceof String) {
				cell.setImage(imgFolder);
			}
		}
		cell.setSelected(isSelected);

		return cell;
	}
}