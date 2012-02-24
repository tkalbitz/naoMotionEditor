/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.panel;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.frame.Actuator;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

public class BodyPartAction extends AbstractAction {

    private static final long serialVersionUID = 5436795719657355991L;

    private final BodyPart bodyPart;

	private MainApplication app;
    
    public BodyPartAction(BodyPart part) {
        bodyPart = part;
        putValue(Action.SELECTED_KEY, false);
        putValue(NAME, bodyPart.name);
    }
    
    public void actionPerformed(ActionEvent e) {
    }
    
    @Override
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
        
        if(!key.equals(SELECTED_KEY)) {
            return;
        }
        
        if(!(newValue instanceof Boolean)) {
            return;
        }
        
		if(app == null) {
			app = MainApplication.injector.getInstance(MainApplication.class);
    	}
        
        /* deactivate only the action not all dependent parts if a part gets deselected */
        if((Boolean)newValue == false) {
            for (BodyPart part : bodyPart.dependendParts) {
                Object o = part.action.getValue(SELECTED_KEY);
                
                if(o instanceof Boolean && (Boolean)o == false) {
                	/* de-/activate the record action */
                	recordActionState();
                    return;
                }
            }
        }
        
        float [] angles = null;
        try {
			angles = MainApplication.comm.getBodyAngles();
		} catch (IOException e) {
			e.printStackTrace();
		}

    	float stiffness = (Boolean)newValue == false ? NaoConstants.STIFF_MAX : NaoConstants.STIFF_MIN;

    	try {
	        for (Actuator a : bodyPart.dependendActuators) {
	    		/* if the stiff button is set do nothing */
	    		if(!app.isInitialized() || app.isStiff()) {
	    			continue;
	    		}
	        	
				MainApplication.comm.setJointPosition(a.position, angles[a.position], 0);
				MainApplication.comm.setJointStiffness(a.position, stiffness, 0);
			}
	
	        /* set dependent parts */
	        for (BodyPart part : bodyPart.dependendParts) {
	            part.action.putValue(SELECTED_KEY, newValue);

	    		/* if the stiff button is set do nothing */
	    		if(!app.isInitialized() || app.isStiff()) {
	    			continue;
	    		}

	            for (Actuator a : part.dependendActuators) {
					MainApplication.comm.setJointPosition(a.position, angles[a.position], 0);
					MainApplication.comm.setJointStiffness(a.position, stiffness, 0);
				}
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
		}

    	/* de-/activate the record action */
    	recordActionState();
    }

	private void recordActionState() {
		if(app == null) {
			app = MainApplication.injector.getInstance(MainApplication.class);
    	}
    	
		if(!app.isInitialized()) {
			return;
		}
		
    	for (BodyPart p : BodyPart.values()) {
    		Object o = p.action.getValue(SELECTED_KEY);
    		
    		if(!(o instanceof Boolean)) {
				continue;
			}
    		
    		boolean isSelected = (Boolean)o;
    		
    		if(isSelected) {
    			app.setEnableRecordAction(true);
    			return;
    		}
		}
    	
		app.setEnableRecordAction(false);
	}
}
