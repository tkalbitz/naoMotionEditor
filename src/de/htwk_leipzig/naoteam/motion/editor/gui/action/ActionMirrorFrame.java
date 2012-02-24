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
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.frame.Actuator;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.ScriptingTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Record the current position of the nao and store it.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionMirrorFrame extends AbstractAction  {

    /** controller to manipulate frames */
    protected MainApplication app;
    
    private JList<NaoFrame> frameList;

    @Inject
    ActionMirrorFrame(IImageLoader loader, MainApplication appController) {
        super("Mirror Frame");
        final String name = "preferences-desktop-locale.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        putValue(SHORT_DESCRIPTION, "Duplicate and mirror the current Frame");
        app = appController;
    }

    public void actionPerformed(ActionEvent e) {
    	NaoFrame curFrame = app.getCurrentSelectedFrame();
    	LinkedList<Float> acValList = new LinkedList<Float>();

    	float[] acVals = curFrame.getAllActuatorPos();
    	for (float ac : acVals) {
			acValList.add(ac);
		}

    	Collections.swap(acValList, Actuator.LAnklePitch.position, Actuator.RAnklePitch.position);
    	Collections.swap(acValList, Actuator.LAnkleRoll.position, Actuator.RAnkleRoll.position);
    	Collections.swap(acValList, Actuator.LElbowRoll.position, Actuator.RElbowRoll.position);
    	Collections.swap(acValList, Actuator.LElbowYaw.position, Actuator.RElbowYaw.position);
    	Collections.swap(acValList, Actuator.LHipPitch.position, Actuator.RHipPitch.position);
    	Collections.swap(acValList, Actuator.LHipRoll.position, Actuator.RHipRoll.position);
    	Collections.swap(acValList, Actuator.LHipYawPitch.position, Actuator.RHipYawPitch.position);
    	Collections.swap(acValList, Actuator.LKneePitch.position, Actuator.RKneePitch.position);
    	Collections.swap(acValList, Actuator.LShoulderPitch.position, Actuator.RShoulderPitch.position);
    	Collections.swap(acValList, Actuator.LShoulderRoll.position, Actuator.RShoulderRoll.position);
    	
    	Float[] newAcVals = acValList.toArray(new Float[0]);
    	
    	Actuator[] acs = Actuator.values();
		for (int i = 0; i < acs.length; i++) {
			if(acs[i].name().contains("Roll")||acs[i].name().contains("Yaw")&&!acs[i].name().contains("Hip")) {
				acVals[i] = - newAcVals[i];
			} else {
				acVals[i] = newAcVals[i];
			}
		}
		
    	NaoFrame frame = new NaoFrame(acVals);
    	frame.useOwnTransformation(curFrame.useOwnTransformation());

    	if(curFrame.getTransformationStrategy() instanceof ScriptingTransformationStrategy) {
			frame = frame.setTransformationStrategy(new ScriptingTransformationStrategy((ScriptingTransformationStrategy) curFrame.getTransformationStrategy()));
    	}
    	
    	int selIndex = frameList.getSelectedIndex();
    	
    	if(selIndex == -1) {
			return;
		}
    	
    	app.createUndoStep();
    	app.getFrameList().add(selIndex, frame);
    	app.getFrameList().remove(selIndex + 1);
    	frameList.setSelectedIndex(selIndex);
    }

	public void setFrameList(JList<NaoFrame> frameList) {
		this.frameList = frameList;
	}

	public JList<NaoFrame> getFrameList() {
		return frameList;
	}
}
