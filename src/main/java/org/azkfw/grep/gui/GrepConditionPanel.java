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

import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Kawakicchi
 *
 */
public class GrepConditionPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = 2498823616809145690L;

	private static final int COMPONENT_HEIGHT = 24;
	private static final int COMPONENT_MARGIN = 6;
	private static final int COMPONENT_SPACE = 4;
	
	private JLabel lblKeyword;
	private JTextField txtKeyword;
	private JLabel lblFileName;
	private JTextField txtFileName;
	private JLabel lblDirectory;
	private JTextField txtDirectory;
	private JButton btnDirectory;
	
	public GrepConditionPanel() {
		setLayout(null);
		
		int x = COMPONENT_MARGIN;
		int y = COMPONENT_MARGIN;
		
		lblKeyword = new JLabel("Containing text:");
		lblKeyword.setLocation(x, y);
		add(lblKeyword);
		y += COMPONENT_HEIGHT;
		txtKeyword = new JTextField("String");
		txtKeyword.setLocation(x, y);
		add(txtKeyword);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;
		
		lblFileName = new JLabel("File name patterns (separated by comma):");
		lblFileName.setLocation(x, y);
		add(lblFileName);
		y += COMPONENT_HEIGHT;
		txtFileName = new JTextField("*.java");
		txtFileName.setLocation(x, y);
		add(txtFileName);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;
		
		lblDirectory = new JLabel("Target directorys (separated by semicolon):");
		lblDirectory.setLocation(x, y);
		add(lblDirectory);
		y += COMPONENT_HEIGHT;
		txtDirectory = new JTextField(".");
		txtDirectory.setLocation(x, y);
		add(txtDirectory);
		btnDirectory = new JButton("...");
		btnDirectory.setSize(40, COMPONENT_HEIGHT);
		add(btnDirectory);
		y += COMPONENT_HEIGHT + COMPONENT_SPACE;
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				Insets insets = getInsets();
				int width = getWidth() - (insets.left + insets.right);
				//int height = getHeight() - (insets.top + insets.bottom);
				
				lblKeyword.setSize(width-(COMPONENT_MARGIN*2), COMPONENT_HEIGHT);
				txtKeyword.setSize(width-(COMPONENT_MARGIN*2), COMPONENT_HEIGHT);

				lblFileName.setSize(width-(COMPONENT_MARGIN*2), COMPONENT_HEIGHT);
				txtFileName.setSize(width-(COMPONENT_MARGIN*2), COMPONENT_HEIGHT);

				lblDirectory.setSize(width-(COMPONENT_MARGIN*2), COMPONENT_HEIGHT);
				txtDirectory.setSize(width-(COMPONENT_MARGIN*2)-40, COMPONENT_HEIGHT);
				btnDirectory.setLocation(width-(40+COMPONENT_MARGIN), txtDirectory.getY());
			}
		});
	}
}
