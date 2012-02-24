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
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.Actuator;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;
import de.htwk_leipzig.naoteam.motion.editor.gui.panel.BodyPart;

/**
 * Record the current position of the nao and store it.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionUpdateFrame extends AbstractAction  {

    /** communication with the nao is done by this interface */
    private INaoCommunication comm;

    /** controller to manipulate frames */
    protected MainApplication app;

    @Inject
    ActionUpdateFrame(IImageLoader loader, MainApplication appController) {
        super("Update Position");
        final String name = "view-refresh.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        app = appController;
        putValue(SHORT_DESCRIPTION, "Update the current position of the robot.");
    }

    public void actionPerformed(ActionEvent e) {

        List<BodyPart> bodyParts = BodyPart.Body.dependendParts;
        final NaoFrame oldFrame = app.getCurrentSelectedFrame();
        float [] readedPos = null; 
        final float [] newPos = oldFrame.getAllActuatorPos();

        try {
			readedPos = getComm().getBodyAngles();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < readedPos.length; i++) {
			System.out.println(readedPos[i]);
		}

        /* there is a frame */
        if(oldFrame != NaoFrame.EMPTY_FRAME) {
             bodyParts = app.getCurrentSelectedBodyParts();

             for (final BodyPart bodyPart : bodyParts) {

                /* if the body part is not composite just restore the angle */
                if(bodyPart.dependendParts.isEmpty()) {
                    restoreOldBodyPartValues(bodyPart, readedPos, newPos);
                    continue;
                }

                /* there are dependend part. process these */
                for (final BodyPart depBodyPart : bodyPart.dependendParts) {
                    restoreOldBodyPartValues(depBodyPart, readedPos, newPos);
                }
            }
        }

        NaoFrame frame = new NaoFrame(newPos, bodyParts);
        frame.useOwnTransformation(oldFrame.useOwnTransformation());
        frame = frame.setTransformationStrategy(oldFrame.getTransformationStrategy());
        app.createUndoStep();
        
        int frameIndex = app.getCurrentSelectedFrameIndex();
        app.getFrameList().add(frameIndex + 1, frame);
        app.getFrameList().remove(frameIndex);
        app.setSelectedFrame(frame);
    }

    private void restoreOldBodyPartValues(BodyPart part, float[] readedPos, float[] newPos) {
        for (final Actuator ac : part.dependendActuators) {
            newPos[ac.position] = readedPos[ac.position];
        }
    }

	public void setComm(INaoCommunication comm) {
		this.comm = comm;
	}

	public INaoCommunication getComm() {
		return comm;
	}
}
