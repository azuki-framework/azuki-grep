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
	private JProgressBar progress;
	
	public StatusBar() {
		setLayout(null);
		
		label = new JLabel();
		label.setLocation(0, 0);
		add(label);
		
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

	private void resize() {
		Insets insets = getInsets();
		int width = getWidth() - (insets.left + insets.right);
		int height = getHeight() - (insets.top + insets.bottom);
		
		label.setSize(width-160, height);
		progress.setBounds(width-200-4, 2, 200, height-4);
	}
}
