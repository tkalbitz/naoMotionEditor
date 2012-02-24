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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Set the robot in the current frame.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionPlayAnim extends AbstractAction  {

	/** communication with the nao is done by this interface */
    private INaoCommunication comm;

    /** controller to manipulate frames */
    protected MainApplication app;

    @Inject
    ActionPlayAnim(IImageLoader loader, MainApplication appController) {
        super("Play anim on robot");
        final String name = "player_play.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        app = appController;
        putValue(SHORT_DESCRIPTION, "Display the complete animation.");
    }

    public static List<NaoFrame> calculateFrames(MainApplication app) {
    	Iterator<NaoFrame> it = app.getFrameList().iterator();
    	Iterator<NaoFrame> itNext = app.getFrameList().iterator();

    	List<NaoFrame> resFrames = new LinkedList<NaoFrame>();
    	
    	/* we displayed already the first */
//    	if(it.hasNext()) {
//    		it.next();
//    		itNext.next();
//    	}
    	
    	/* must be one ahead */
		NaoFrame nextFrame = null;
    	if(itNext.hasNext()) {
    		nextFrame = itNext.next();
    	}
    	
    	for (; it.hasNext();) {
			NaoFrame frame = it.next();
			resFrames.add(frame);
			
	    	if(itNext.hasNext()) {
	    		nextFrame = itNext.next();
	    	}
			
            try {
            	IFrameTransformationStrategy strg = frame.getTransformationStrategy();
            	
            	if(!frame.useOwnTransformation()) {
            		strg = app.getStandardStrategy();
            	}
            	
            	/* create frames for the gap ;-) */
            	if(it.hasNext()) {
            		List<NaoFrame> frames = strg.transform(frame, nextFrame);
            		resFrames.addAll(frames);
            	}
            } catch (Exception ex) {
            	ex.printStackTrace();
            	JOptionPane.showMessageDialog(app.getMainWindow(), "Your scipt contains errors. See console for full stacktrace.");
            	return null;
    		}			
		}
    	
    	return resFrames;
    }
    
    public void actionPerformed(ActionEvent e) {
    	/* langsames anfahren des start frames */
    	try {
	    	NaoFrame firstFrame = app.getFrameList().getFirst();
	    	if(firstFrame != null) {
	    		firstFrame.moveSlowlyTo(comm);
	    	}
	    	
	    	List<NaoFrame> frames = calculateFrames(app);
	    	for (NaoFrame frame : frames) {
				comm.playFrame(frame);
			}
    	} catch(Exception ex) {
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
