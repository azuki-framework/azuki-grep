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
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.azkfw.component.text.TextEditor;
import org.azkfw.component.text.TextGradationsView;
import org.azkfw.component.text.TextLineNumberView;
import org.azkfw.grep.FindFile;
import org.azkfw.grep.Grep;
import org.azkfw.grep.GrepCondition;
import org.azkfw.grep.GrepEvent;
import org.azkfw.grep.GrepListener;
import org.azkfw.grep.GrepResult;
import org.azkfw.grep.gui.FileTree.FindFileObject;

/**
 * @author Kawakicchi
 *
 */
public class GrepFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 3966217588565085514L;

	public static void main(final String[] args) {
		GrepFrame frm = new GrepFrame();
		frm.setVisible(true);
	}
	
	private Grep grep;
	
	private JSplitPane splMain;
	private JSplitPane splSub;
	
	private GrepConditionPanel pnlCondition;
	private FileTree fileTree;
	private JScrollPane fileTreeScroll;
	
	private TextEditor textEditer;
	private JScrollPane textEditerScroll;

	private JMenuBar menuBar;
	private JMenu menuFile;

	private StatusBar statusBar;
	
	public GrepFrame() {
		setTitle("AzukiGrep");
		setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		pnlCondition = new GrepConditionPanel();
		
		fileTree = new FileTree();
		fileTreeScroll = new JScrollPane(fileTree);

		textEditer = new TextEditor();
		textEditerScroll = new JScrollPane(textEditer);
		textEditerScroll.setColumnHeaderView(new TextGradationsView(textEditer));
		textEditerScroll.setRowHeaderView(new TextLineNumberView(textEditer));
		
		splSub = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splSub.setTopComponent(pnlCondition);
		splSub.setBottomComponent(fileTreeScroll);
		splSub.setDividerLocation(200);
		//splSub.setBorder(new LineBorder(Color.RED, 2, true));
		splSub.setBorder(null);
		
		splMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splMain.setLocation(0, 0);
		splMain.setLeftComponent(splSub);
		splMain.setRightComponent(textEditerScroll);
		splMain.setDividerLocation(360);
		splMain.setBorder(null);

		add(splMain);
		
		statusBar = new StatusBar();
		add(statusBar);
		
		// Menu -------------------------------------
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		// ==========================================
		
		grep = new Grep();
		grep.addGrepListener(new GrepListener() {
			@Override
			public void grepStart(final GrepEvent e) {

				fileTree.setRootDirectory(e.getSource().getCondition().getTargetDirectory());
			}
			@Override
			public void grepFinished(final GrepEvent e, final GrepResult r) {
				String message = String.format("%d 件見つかりました", e.getSource().getStatistics().getFindFileCount());
				statusBar.setMessage(message);
				
				System.out.println(String.format("%.2f sec", (double) (r.getProcessingNanoTime()) / 1000000000.f));

				fileTree.expandAll();
			}
			@Override
			public void grepFindFile(final GrepEvent e, final FindFile f) {
				String message = String.format("%s", f.getFile().getName());
				statusBar.setMessage(message);
				
				fileTree.addFile(f);
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				Insets insets = getInsets();
				int width = getWidth() - (insets.left + insets.right);
				int height = getHeight() - (insets.top + insets.bottom) - menuBar.getHeight();
				
				splMain.setSize(width, height-24);
				
				statusBar.setLocation(0, height-24);
				statusBar.setSize(width, 24);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				GrepCondition condition = new GrepCondition();
				condition.setTargetDirectory(new File("."));
				//condition.setTargetDirectory(new File("/Users/Kawakicchi/git/azuki-grep"));
				//condition.setTargetDirectory(new File("/Users/Kawakicchi/iPhone workspace"));
				
				grep.start(condition);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
		});
		
		fileTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					JTree tree = (JTree) e.getSource();
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());

					if (null != path) {
						Object obj = path.getLastPathComponent();
						if (obj instanceof DefaultMutableTreeNode) {
							Object obj2 = ((DefaultMutableTreeNode) obj).getUserObject();
							if (obj2 instanceof FindFileObject) {
								FindFile ff = ((FindFileObject) obj2).getFindFile();
								try {
									textEditer.setText( FileUtils.readFileToString(ff.getFile(), ff.getCharset()) );
									textEditer.setCaretPosition(ff.getMatchs().get(0).getStart());
								} catch (IOException ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rt = env.getMaximumWindowBounds();
		rt.height -= 200;
		setBounds(rt);
	}
}
