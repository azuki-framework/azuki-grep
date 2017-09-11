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
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.azkfw.grep.entity.ContainingText;
import org.azkfw.grep.entity.DirectoryNamePattern;
import org.azkfw.grep.entity.FileNamePattern;
import org.azkfw.grep.entity.GrepCondition;
import org.azkfw.grep.entity.TargetDirectory;

/**
 * @author Kawakicchi
 */
public class GrepConditionPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 2498823616809145690L;

	private static final int COMPONENT_HEIGHT = 24;

	private static final int COMPONENT_MARGIN = 6;

	private static final int COMPONENT_SPACE = 4;

	private final JLabel lblContainingText;

	private final JTextField txtContainingText1;

	private final JTextField txtContainingText2;

	private final JTextField txtContainingText3;

	private final JLabel lblMarkingText;

	private final JTextField txtMarkingText1;

	private final JTextField txtMarkingText2;

	private final JLabel lblFileNamePatterns;

	private final JTextField txtFileNamePatterns;

	private final JLabel lblExcludeFileNamePatterns;

	private final JTextField txtExcludeFileNamePatterns;

	private final JLabel lblTargetDirectorys;

	private final JTextField txtTargetDirectorys;

	private final JButton btnTargetDirectorys;

	private final JLabel lblExcludeDirectoryNamePatterns;

	private final JTextField txtExcludeDirectoryNamePatterns;

	private final JButton btnSearch;

	private List<GrepConditionPanelListener> listeners;

	public GrepConditionPanel() {
		setLayout(null);

		int x = COMPONENT_MARGIN;
		int y = COMPONENT_MARGIN;

		lblContainingText = new JLabel("Containing text:");
		lblContainingText.setLocation(x, y);
		add(lblContainingText);
		y += COMPONENT_HEIGHT;
		txtContainingText1 = new JTextField("");
		txtContainingText1.setLocation(x, y);
		add(txtContainingText1);
		y += COMPONENT_HEIGHT;
		txtContainingText2 = new JTextField("");
		txtContainingText2.setLocation(x, y);
		add(txtContainingText2);
		y += COMPONENT_HEIGHT;
		txtContainingText3 = new JTextField("");
		txtContainingText3.setLocation(x, y);
		add(txtContainingText3);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;

		lblMarkingText = new JLabel("Marking text:");
		lblMarkingText.setLocation(x, y);
		add(lblMarkingText);
		y += COMPONENT_HEIGHT;
		txtMarkingText1 = new JTextField("");
		txtMarkingText1.setLocation(x, y);
		txtMarkingText1.setBackground(new Color(240, 140, 255));
		add(txtMarkingText1);
		y += COMPONENT_HEIGHT;
		txtMarkingText2 = new JTextField("");
		txtMarkingText2.setLocation(x, y);
		txtMarkingText2.setBackground(new Color(30, 255, 180));
		add(txtMarkingText2);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;

		lblFileNamePatterns = new JLabel("File name patterns (separated by comma):");
		lblFileNamePatterns.setLocation(x, y);
		add(lblFileNamePatterns);
		y += COMPONENT_HEIGHT;
		txtFileNamePatterns = new JTextField("");
		txtFileNamePatterns.setLocation(x, y);
		add(txtFileNamePatterns);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;

		lblExcludeFileNamePatterns = new JLabel("Exclude file name patterns (separated by comma):");
		lblExcludeFileNamePatterns.setLocation(x, y);
		add(lblExcludeFileNamePatterns);
		y += COMPONENT_HEIGHT;
		txtExcludeFileNamePatterns = new JTextField("");
		txtExcludeFileNamePatterns.setLocation(x, y);
		add(txtExcludeFileNamePatterns);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;

		lblTargetDirectorys = new JLabel("Target directorys (separated by semicolon):");
		lblTargetDirectorys.setLocation(x, y);
		add(lblTargetDirectorys);
		y += COMPONENT_HEIGHT;
		txtTargetDirectorys = new JTextField("");
		txtTargetDirectorys.setLocation(x, y);
		add(txtTargetDirectorys);
		btnTargetDirectorys = new JButton("...");
		btnTargetDirectorys.setSize(40, COMPONENT_HEIGHT);
		add(btnTargetDirectorys);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;

		lblExcludeDirectoryNamePatterns = new JLabel("Exclude directory name patterns (separated by comma):");
		lblExcludeDirectoryNamePatterns.setLocation(x, y);
		add(lblExcludeDirectoryNamePatterns);
		y += COMPONENT_HEIGHT;
		txtExcludeDirectoryNamePatterns = new JTextField("");
		txtExcludeDirectoryNamePatterns.setLocation(x, y);
		add(txtExcludeDirectoryNamePatterns);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;

		btnSearch = new JButton("Search");
		btnSearch.setLocation(x, y);
		btnSearch.setSize(80, COMPONENT_HEIGHT);
		add(btnSearch);

		listeners = new ArrayList<GrepConditionPanelListener>();

		addListener();
	}

	public void addGrepConditionPanelListener(final GrepConditionPanelListener listener) {
		listeners.add(listener);
	}

	public String getMarkingText1() {
		return txtMarkingText1.getText();
	}

	public String getMarkingText2() {
		return txtMarkingText2.getText();
	}

	public void setCondition(final GrepCondition condition) {
		List<ContainingText> texts = condition.getContainingTexts();
		if (0 < texts.size()) {
			txtContainingText1.setText(texts.get(0).getValue());
		}
		if (1 < texts.size()) {
			txtContainingText2.setText(texts.get(1).getValue());
		}
		if (2 < texts.size()) {
			txtContainingText3.setText(texts.get(2).getValue());
		}

		List<FileNamePattern> fileNamePatterns = condition.getFileNamePatterns();
		StringBuffer strFileNamePatterns = new StringBuffer();
		for (FileNamePattern pattern : fileNamePatterns) {
			if (0 < strFileNamePatterns.length()) {
				strFileNamePatterns.append(", ");
			}
			strFileNamePatterns.append(pattern.getValue());
		}
		txtFileNamePatterns.setText(strFileNamePatterns.toString());

		List<FileNamePattern> excludeFileNamePatterns = condition.getExcludeFileNamePatterns();
		StringBuffer strExcludeFileNamePatterns = new StringBuffer();
		for (FileNamePattern pattern : excludeFileNamePatterns) {
			if (0 < strExcludeFileNamePatterns.length()) {
				strExcludeFileNamePatterns.append(", ");
			}
			strExcludeFileNamePatterns.append(pattern.getValue());
		}
		txtExcludeFileNamePatterns.setText(strExcludeFileNamePatterns.toString());

		List<TargetDirectory> targetDirectorys = condition.getTargetDirectorys();
		StringBuffer strTargetDirectorys = new StringBuffer();
		for (TargetDirectory directory : targetDirectorys) {
			if (0 < strTargetDirectorys.length()) {
				strTargetDirectorys.append("; ");
			}
			strTargetDirectorys.append(directory.getValue());
		}
		txtTargetDirectorys.setText(strTargetDirectorys.toString());

		List<DirectoryNamePattern> excludeDirectoryNamePatterns = condition.getExcludeDirectoryNamePatterns();
		StringBuffer strExcludeDirectoryNamaePatterns = new StringBuffer();
		for (DirectoryNamePattern pattern : excludeDirectoryNamePatterns) {
			if (0 < strExcludeDirectoryNamaePatterns.length()) {
				strExcludeDirectoryNamaePatterns.append(", ");
			}
			strExcludeDirectoryNamaePatterns.append(pattern.getValue());
		}
		txtExcludeDirectoryNamePatterns.setText(strExcludeDirectoryNamaePatterns.toString());
	}

	public GrepCondition getCondition() {
		GrepCondition condition = new GrepCondition();

		List<ContainingText> containingTexts = new ArrayList<ContainingText>();
		ContainingText containingText1 = new ContainingText();
		containingText1.setValue(txtContainingText1.getText());
		ContainingText containingText2 = new ContainingText();
		containingText2.setValue(txtContainingText2.getText());
		ContainingText containingText3 = new ContainingText();
		containingText3.setValue(txtContainingText3.getText());
		containingTexts.add(containingText1);
		containingTexts.add(containingText2);
		containingTexts.add(containingText3);
		condition.setContainingTexts(containingTexts);

		String[] split = null;

		List<FileNamePattern> fileNamePatterns = new ArrayList<FileNamePattern>();
		split = txtFileNamePatterns.getText().split("[\\s]*,[\\s]*");
		for (String s : split) {
			FileNamePattern fileNamePattern = new FileNamePattern();
			fileNamePattern.setValue(s);
			fileNamePatterns.add(fileNamePattern);
		}
		condition.setFileNamePatterns(fileNamePatterns);

		List<FileNamePattern> excludeFileNamePatterns = new ArrayList<FileNamePattern>();
		split = txtExcludeFileNamePatterns.getText().split("[\\s]*,[\\s]*");
		for (String s : split) {
			FileNamePattern fileNamePattern = new FileNamePattern();
			fileNamePattern.setValue(s);
			excludeFileNamePatterns.add(fileNamePattern);
		}
		condition.setExcludeFileNamePatterns(excludeFileNamePatterns);

		List<TargetDirectory> targetDirectorys = new ArrayList<TargetDirectory>();
		split = txtTargetDirectorys.getText().split("[\\s]*;[\\s]*");
		for (String s : split) {
			TargetDirectory targetDirectory = new TargetDirectory();
			targetDirectory.setValue(s);
			targetDirectorys.add(targetDirectory);
		}
		condition.setTargetDirectorys(targetDirectorys);

		List<DirectoryNamePattern> excludeDirectoryNamePatterns = new ArrayList<DirectoryNamePattern>();
		split = txtExcludeDirectoryNamePatterns.getText().split("[\\s]*,[\\s]*");
		for (String s : split) {
			DirectoryNamePattern directoryNamePattern = new DirectoryNamePattern();
			directoryNamePattern.setValue(s);
			excludeDirectoryNamePatterns.add(directoryNamePattern);
		}
		condition.setExcludeDirectoryNamePatterns(excludeDirectoryNamePatterns);

		return condition;
	}

	private void search() {
		GrepCondition condition = getCondition();
		synchronized (listeners) {
			for (GrepConditionPanelListener listener : listeners) {
				listener.grepConditionPanelSearch(condition);
			}
		}
	}

	private void selectTargetDirectory() {
		JFileChooser filechooser = new JFileChooser(txtTargetDirectorys.getText());
		filechooser.setMultiSelectionEnabled(true);
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int selected = filechooser.showDialog(this, "Select");
		if (selected == JFileChooser.APPROVE_OPTION) {
			File[] files = filechooser.getSelectedFiles();
			StringBuffer s = new StringBuffer();
			for (File file : files) {
				if (0 < s.length()) {
					s.append("; ");
				}
				s.append(file.getAbsolutePath());
			}
			txtTargetDirectorys.setText(s.toString());
		}
	}

	private KeyListener textFieldSearchKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				search();
				e.consume();
			}
		}
	};

	private void addListener() {

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				Insets insets = getInsets();
				int width = getWidth() - (insets.left + insets.right);
				// int height = getHeight() - (insets.top + insets.bottom);

				lblContainingText.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtContainingText1.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtContainingText2.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtContainingText3.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);

				lblMarkingText.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtMarkingText1.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtMarkingText2.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);

				lblFileNamePatterns.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtFileNamePatterns.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);

				lblExcludeFileNamePatterns.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtExcludeFileNamePatterns.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);

				lblTargetDirectorys.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtTargetDirectorys.setSize(width - (COMPONENT_MARGIN * 2) - 40, COMPONENT_HEIGHT);
				btnTargetDirectorys.setLocation(width - (40 + COMPONENT_MARGIN), txtTargetDirectorys.getY());

				lblExcludeDirectoryNamePatterns.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);
				txtExcludeDirectoryNamePatterns.setSize(width - (COMPONENT_MARGIN * 2), COMPONENT_HEIGHT);

				btnSearch.setLocation(width - (80 + COMPONENT_MARGIN), btnSearch.getY());
			}
		});

		btnTargetDirectorys.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				selectTargetDirectory();
			}
		});
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				search();
			}
		});

		txtContainingText1.addKeyListener(textFieldSearchKeyListener);
		txtContainingText2.addKeyListener(textFieldSearchKeyListener);
		txtContainingText3.addKeyListener(textFieldSearchKeyListener);
		txtMarkingText1.addKeyListener(textFieldSearchKeyListener);
		txtMarkingText2.addKeyListener(textFieldSearchKeyListener);
		txtFileNamePatterns.addKeyListener(textFieldSearchKeyListener);
		txtExcludeFileNamePatterns.addKeyListener(textFieldSearchKeyListener);
		txtTargetDirectorys.addKeyListener(textFieldSearchKeyListener);
		txtExcludeDirectoryNamePatterns.addKeyListener(textFieldSearchKeyListener);

		txtTargetDirectorys.setTransferHandler(new TransferHandler() {

			/** serialVersionUID */
			private static final long serialVersionUID = 3632258793369763814L;

			@Override
			public boolean canImport(final TransferSupport support) {
				if (!support.isDrop()) {
					return false;
				}
				if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					return false;
				}
				return true;
			}

			@Override
			public boolean importData(TransferSupport support) {
				if (!canImport(support)) {
					return false;
				}

				Transferable t = support.getTransferable();
				try {
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

					StringBuffer fileList = new StringBuffer();
					for (File file : files) {
						if (0 < fileList.length()) {
							fileList.append("; ");
						}
						fileList.append(file.getAbsolutePath());
					}

					String old = txtTargetDirectorys.getText().trim();
					if (0 < old.length() && !old.endsWith(";")) {
						old += "; ";
					}

					String add = old + fileList.toString();

					txtTargetDirectorys.setText(add);
				} catch (UnsupportedFlavorException | IOException ex) {
					ex.printStackTrace();
				}
				return true;
			}
		});
	}
}
