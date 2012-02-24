/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
    package de.htwk_leipzig.naoteam.motion.editor.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Record the current position of the nao and store it.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionDuplicateFrame extends AbstractAction  {

    /** controller to manipulate frames */
    protected MainApplication app;
    
    private JList<NaoFrame> frameList;

    @Inject
    ActionDuplicateFrame(IImageLoader loader, MainApplication appController) {
        super("Duplicate Frame");
        final String name = "edit-copy.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        putValue(SHORT_DESCRIPTION, "Duplicate current Frame");
        app = appController;
    }

    public void actionPerformed(ActionEvent e) {
    	app.createUndoStep();
    	NaoFrame frame = new NaoFrame(app.getCurrentSelectedFrame());
    	int selIndex = frameList.getSelectedIndex();
    	
    	if(selIndex == -1) {
			return;
		}
    	
    	app.getFrameList().add(selIndex, frame);
    	frameList.setSelectedIndex(selIndex);
    }

	public void setFrameList(JList<NaoFrame> frameList) {
		this.frameList = frameList;
	}

	public JList<NaoFrame> getFrameList() {
		return frameList;
	}
}
