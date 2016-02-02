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

import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXB;

import net.arnx.jsonic.JSON;

import org.apache.commons.io.FileUtils;
import org.azkfw.component.text.TextGradationsView;
import org.azkfw.component.text.TextLineNumberView;
import org.azkfw.grep.Grep;
import org.azkfw.grep.GrepEvent;
import org.azkfw.grep.GrepListener;
import org.azkfw.grep.entity.GrepMatchFile;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.GrepResult;
import org.azkfw.grep.gui.FileTree.MatchFileObject;

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
	private GrepResult grepResult;
	
	private JSplitPane splMain;
	private JSplitPane splSub;
	
	private GrepConditionPanel pnlCondition;
	private FileTree fileTree;
	private JScrollPane fileTreeScroll;
	
	private GrepTextEditer textEditer;
	private JScrollPane textEditerScroll;

	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuFileExport;
	private JMenuItem menuFileExportExcel;
	private JMenuItem menuFileExportXML;
	private JMenuItem menuFileExportHTML;
	private JMenuItem menuFileExit;
	private JMenu menuHelp;
	private JMenuItem menuHelpVersion;

	private StatusBar statusBar;
	
	public GrepFrame() {
		setTitle("AzukiGrep");
		setLayout(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		grep = new Grep();
		
		initMenuBar();
		initStatusBar();
		initComponent();
		
		addListener();
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rt = env.getMaximumWindowBounds();
		rt.height -= 200;
		setBounds(rt);
	}
	
	private void initMenuBar() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		menuFileExport = new JMenu("Export");
		menuFile.add(menuFileExport);
		
		menuFileExportExcel = new JMenuItem("Excel...");
		menuFileExport.add(menuFileExportExcel);
		menuFileExportXML = new JMenuItem("XML...");
		menuFileExport.add(menuFileExportXML);
		menuFileExportHTML = new JMenuItem("HTML...");
		menuFileExport.add(menuFileExportHTML);
		
		menuFile.addSeparator();
		
		menuFileExit = new JMenuItem("Exit");
		menuFile.add(menuFileExit);
		
		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		
		menuHelpVersion = new JMenuItem("Version");
		menuHelp.add(menuHelpVersion);
	}
	
	private void initStatusBar() {
		statusBar = new StatusBar();
		add(statusBar);
	}
	
	private void initComponent() {
		pnlCondition = new GrepConditionPanel();
		
		fileTree = new FileTree();
		fileTreeScroll = new JScrollPane(fileTree);

		textEditer = new GrepTextEditer();
		textEditerScroll = new JScrollPane(textEditer);
		textEditerScroll.setColumnHeaderView(new TextGradationsView(textEditer));
		textEditerScroll.setRowHeaderView(new GrepTextLineNumberView(textEditer, textEditerScroll.getVerticalScrollBar()));
		
		splSub = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splSub.setTopComponent(pnlCondition);
		splSub.setBottomComponent(fileTreeScroll);
		splSub.setDividerLocation(290);
		//splSub.setBorder(new LineBorder(Color.RED, 2, true));
		splSub.setBorder(null);
		
		splMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splMain.setLocation(0, 0);
		splMain.setLeftComponent(splSub);
		splMain.setRightComponent(textEditerScroll);
		splMain.setDividerLocation(360);
		splMain.setBorder(null);

		add(splMain);
	}
	
	private void exit() {
		dispose();
	}
	
	private void load() {
		File file = new File("condition.xml");
		GrepCondition condition = JAXB.unmarshal(file, GrepCondition.class);
		
		pnlCondition.setCondition(condition);
	}
	
	private void save() {
		String str = JSON.encode(pnlCondition.getCondition());
		System.out.println(str);
		
		File file = new File("condition.xml");
		JAXB.marshal(pnlCondition.getCondition(), file);
	}
	
	private void reportXML(final GrepResult result, final File file) {
		JAXB.marshal(result, file);
	}
	private void reportHTML(final GrepResult result, final File file) {
		
	}
	private void reportExcel(final GrepResult result, final File file) {
		
	}
	
	private void doReportXML() {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setMultiSelectionEnabled(false);
		filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int selected = filechooser.showSaveDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			reportXML(grepResult, filechooser.getSelectedFile());
		}
	}
	private void doReportHTML() {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setMultiSelectionEnabled(false);
		filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int selected = filechooser.showSaveDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			reportHTML(grepResult, filechooser.getSelectedFile());
		}
	}
	private void doReportExcel() {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setMultiSelectionEnabled(false);
		filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int selected = filechooser.showSaveDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION) {
			reportExcel(grepResult, filechooser.getSelectedFile());
		}
	}
	
	private void addListener() {
		grep.addGrepListener(new GrepListener() {
			@Override
			public void grepStart(final GrepEvent e) {

				statusBar.setProgress(-1);
				fileTree.setRootDirectorys(e.getSource().getCondition().getTargetDirectoryFiles());
			}
			@Override
			public void grepFinished(final GrepEvent e, final GrepResult r) {
				String message = String.format("%d 件見つかりました", e.getSource().getStatistics().getFindFileCount());
				statusBar.setMessage(message);
				
				System.out.println(String.format("%.2f sec", (double) (r.getProcessingNanoTime()) / 1000000000.f));

				fileTree.expandAll();

				statusBar.setProgress(100);
				
				grepResult = r;
			}
			@Override
			public void grepFindFile(final GrepEvent e, final GrepMatchFile f) {
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
				load();
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				save();
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
							if (obj2 instanceof MatchFileObject) {
								GrepMatchFile matchFile = ((MatchFileObject) obj2).getMatchFile();
								try {
									textEditer.setText( FileUtils.readFileToString(matchFile.getFile(), matchFile.getCharset()) );

									// XXX: 
									textEditer.addHighlighter(matchFile.getWords());
									//textEditer.addHighlighter(grep.getCondition().getContainingTexts().get(0).getPattern());

									// XXX: 
									textEditer.setCaretPosition(0);
									// textEditer.setCaretPosition(matchFile.getWords().get(0).getStart());

									statusBar.setLineSeparator(matchFile.getLineSeparatorToString());
									statusBar.setCharset(matchFile.getCharset());

								} catch (IOException ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
		
		pnlCondition.addGrepConditionPanelListener(new GrepConditionPanelListener() {
			@Override
			public void grepConditionPanelSearch(final GrepCondition condition) {
				grep.start(condition);
			}
		});
		
		menuFileExportExcel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doReportExcel();
			}
		});
		menuFileExportHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doReportHTML();
			}
		});
		menuFileExportXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doReportXML();
			}
		});
		
		menuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
	}
		
}
