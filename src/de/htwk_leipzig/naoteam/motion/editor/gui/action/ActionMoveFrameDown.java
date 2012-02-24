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
import java.util.Collections;
import java.util.List;

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
public class ActionMoveFrameDown extends AbstractAction  {

    /** controller to manipulate frames */
    protected MainApplication app;

    /** list which contains the items */
    private JList<NaoFrame> frameList;
    
    @Inject
    ActionMoveFrameDown(IImageLoader loader, MainApplication appController) {
        super("Move frame down");
        final String name = "go-down.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        putValue(SHORT_DESCRIPTION, "Move the current Frame down");
        app = appController;
    }

    public void actionPerformed(ActionEvent e) {
    	app.createUndoStep();

    	int selected = frameList.getSelectedIndex();
    	List<NaoFrame> lst = app.getFrameList();
    	
    	if(selected == -1 || selected == lst.size() - 1) {
			return;
		}
    	
    	Collections.swap(app.getFrameList(), selected, selected + 1);
    	frameList.setSelectedIndex(selected + 1);
    }

	public void setFrameList(JList<NaoFrame> frameList) {
		this.frameList = frameList;
	}

	public JList<NaoFrame> getFrameList() {
		return frameList;
	}
    
    
    
    
    
}
