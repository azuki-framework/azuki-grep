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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author Kawakicchi
 *
 */
public class StatusBar extends JPanel{

	/** serialVersionUID */
	private static final long serialVersionUID = 737315155756183060L;

	private JLabel label;
	private JLabel lblSize;
	private JLabel lblLineSeparator;
	private JLabel lblCharset;
	private JProgressBar progress;
	
	public StatusBar() {
		setLayout(null);
		
		label = new JLabel();
		label.setLocation(0, 0);
		add(label);

		lblLineSeparator = new JLabel();
		lblLineSeparator.setHorizontalAlignment(JLabel.CENTER);
		add(lblLineSeparator);

		lblSize = new JLabel();
		lblSize.setHorizontalAlignment(JLabel.RIGHT);
		add(lblSize);

		lblCharset = new JLabel();
		lblCharset.setHorizontalAlignment(JLabel.CENTER);
		add(lblCharset);

		progress = new JProgressBar();
		add(progress);
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				resize();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}
	
	public void setMessage(final String message) {
		label.setText(message);
	}

	public void setProgress(final int percent) {
		if (0 > percent) {
			progress.setIndeterminate(true);
		} else {
			progress.setIndeterminate(false);
			progress.setValue(percent);
		}
	}

	public void setSize(final long size) {
		lblSize.setText(String.format("%d byte", size));
	}
	public void setLineSeparator(final String lineSeparator) {
		lblLineSeparator.setText(lineSeparator);
	}
	public void setCharset(final String charset) {
		lblCharset.setText(charset);
	}
	
	private void resize() {
		Insets insets = getInsets();
		int width = getWidth() - (insets.left + insets.right);
		int height = getHeight() - (insets.top + insets.bottom);

		label.setSize(width-160, height);

		lblSize.setBounds(width - (100 + 60 + 60 + 160 + 4), 0, 100, height);
		lblLineSeparator.setBounds(width - (60 + 60 + 160 + 4), 0, 60, height);
		lblCharset.setBounds(width - (60 + 160 + 4), 0, 60, height);

		progress.setBounds(width - (160 + 4), 2, 160, height - 4);
	}
}
