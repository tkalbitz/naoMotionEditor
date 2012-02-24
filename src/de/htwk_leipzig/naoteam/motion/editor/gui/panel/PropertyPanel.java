/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.panel;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;
import de.htwk_leipzig.naoteam.motion.editor.gui.border.NaoBorderFactory;

public class PropertyPanel {

    /** contains content */
    private JPanel contentPanel;
    private JPanel stdTransformationPanel;
    private JPanel curTransformationPabel;
	private final MainApplication app;
	private JPanel stdStrategy;
	private JCheckBox useOwn;
	private NaoFrame currentFrame;

    public PropertyPanel(MainApplication ap) {
    	app = ap;
        buildPanel();
    }

    protected void buildPanel() {
        final FormLayout layout = new FormLayout("fill:pref:grow");

        final DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(NaoBorderFactory.getInnerFrameBorder());

        builder.appendSeparator("Standard Transformation");

        stdStrategy = new JPanel(new BorderLayout()); 
        stdStrategy.add(app.getStandardStrategy().getConfigComponent(), BorderLayout.CENTER);

        stdTransformationPanel = new JPanel();
        stdTransformationPanel.setLayout(new BorderLayout());
        stdTransformationPanel.add(stdStrategy, BorderLayout.CENTER);
        builder.append(stdTransformationPanel);

        builder.appendSeparator("Current Transformation");
        useOwn = new JCheckBox("Use own interpolation");
        useOwn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				curTransformationPabel.setVisible(useOwn.isSelected());
				if(currentFrame != null) {
					currentFrame.useOwnTransformation(useOwn.isSelected());
				}
			}
		});
        
        builder.append(useOwn);

        curTransformationPabel = new JPanel();
        curTransformationPabel.setLayout(new BorderLayout());
        JPanel tmp = new JPanel();
        tmp.setSize(stdStrategy.getSize());
        curTransformationPabel.add(tmp, BorderLayout.CENTER);
        builder.append(curTransformationPabel);
        contentPanel = builder.getPanel();
    }

    
    public void setFrame(NaoFrame frame) {
    	currentFrame = frame;
    	useOwn.setSelected(frame.useOwnTransformation());
    	curTransformationPabel.removeAll();
    	curTransformationPabel.add(frame.getTransformationStrategy().getConfigComponent(), BorderLayout.CENTER);
		curTransformationPabel.setVisible(useOwn.isSelected());
		curTransformationPabel.validate();
		curTransformationPabel.repaint();
	}
    
    public JPanel getContentPanel() {
        return contentPanel;
    }

	public void changedStandardStrategy() {
		stdStrategy.removeAll();
        stdStrategy.add(app.getStandardStrategy().getConfigComponent(), BorderLayout.CENTER);
        stdStrategy.validate();
		curTransformationPabel.repaint();
	}
}
