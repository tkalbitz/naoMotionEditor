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

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Set the robot in the current frame.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionStand extends AbstractAction  {

    /** communication with the nao is done by this interface */
    public INaoCommunication comm;

    /** controller to manipulate frames */
    protected MainApplication app;

    @Inject
    ActionStand(IImageLoader loader, MainApplication appController) {
        super("Robot stand");
        final String name = "view-fullscreen.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        app = appController;
        putValue(SHORT_DESCRIPTION, "The robot go into the normal stand.");
    }

    public void actionPerformed(ActionEvent e) {
        final NaoFrame frame = new NaoFrame(NaoConstants.STAND_ANGLES);

        try {
        	frame.moveSlowlyTo(comm);        	
        } catch (Exception ex) {
        	ex.printStackTrace();
		}
        
    }

	public void setComm(INaoCommunication comm) {
		this.comm = comm;
	}

	public INaoCommunication getComm() {
		return comm;
	}
}
