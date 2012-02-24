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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

public class ActionMotionExport extends AbstractAction {

	private static final long serialVersionUID = -2360863360906102247L;
	private final MainApplication app;
	public INaoCommunication comm;

	@Inject
	public ActionMotionExport(IImageLoader loader, MainApplication g) {
		super("Save to motion file");
		final String name = "filesave.png";
		putValue(Action.LARGE_ICON_KEY, loader.large(name));
		putValue(Action.SMALL_ICON, loader.small(name));
		putValue(SHORT_DESCRIPTION, "Save the current animation as motion file.");

		app = g;
	}
	
	private File chooseFile() {
		final Preferences prefs = Preferences.userNodeForPackage(MainApplication.class);
		String searchDir = prefs.get("defaultSearchDir", null);			
		if(searchDir != null) {
			File curDir = new File(searchDir);
			
			if(!curDir.exists() || !curDir.isDirectory()) {
				searchDir = null;
			}
		}
		final JFileChooser chooser = new JFileChooser(searchDir);

		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MotionEditor files (*.mot)", "mot"));
		int result = chooser.showSaveDialog(app.getMainWindow());

		File file = null;
		
		switch (result) {
		case JFileChooser.CANCEL_OPTION:
			return null;
		case JFileChooser.ERROR_OPTION:
			return null;
		}
		
		file = chooser.getSelectedFile();
		app.setCurFile(file);
		
		prefs.put("defaultSearchDir", file.getParent());
		try {
			prefs.flush();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
		return file;
	}
	
	public void setComm(INaoCommunication comm) {
		this.comm = comm;
	}

	public INaoCommunication getComm() {
		return comm;
	}
	
	public void actionPerformed(ActionEvent e) {
		/* langsames anfahren des start frames */
    	try {
	    	NaoFrame firstFrame = app.getFrameList().getFirst();
	    	if(firstFrame != null) {
	    		firstFrame.moveSlowlyTo(comm);
	    	}
	    	System.out.println("startRec");
	    	comm.startRec(app.getFrameList().size());
	    	
	    	List<NaoFrame> frames = calculateFrames(app);
	    	for (NaoFrame frame : frames) {
				comm.playFrame(frame);
			}
	    	comm.stopRec();
	    	System.out.println("stop");
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
		File file = chooseFile();
		
		if(file == null) {
			return;
		}
		
		if(!file.getName().endsWith(".mot")) {
			file = new File(file.getAbsolutePath() + ".mot");
		}		
		
		List<NaoFrame> frames = ActionPlayAnim.calculateFrames(app);
		
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writeMotion(frames, w);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if(w != null) {
				try {
					w.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	 public static List<NaoFrame> calculateFrames(MainApplication app) {
    	Iterator<NaoFrame> it = app.getFrameList().iterator();
    	Iterator<NaoFrame> itNext = app.getFrameList().iterator();

    	List<NaoFrame> resFrames = new LinkedList<NaoFrame>();
    	
    	/* we displayed already the first */
//	    	if(it.hasNext()) {
//	    		it.next();
//	    		itNext.next();
//	    	}
    	
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

	private void writeMotion(List<NaoFrame> frames, BufferedWriter w)
			throws IOException {
		w.write("1\n");
		w.write(String.valueOf(frames.size()) + "\n");
		w.write("0\n");//next movement id
		w.write("0\n");//stand up allowed?
		w.write("0\n");//allow to move head?
		
		for (NaoFrame frame : frames) {
			int cnt=0;
			for(float f : frame.getAllActuatorPos()) {
				String str=String.format(Locale.ENGLISH,"%+5.3f,", f);
				if(cnt==21){
					str=String.format(Locale.ENGLISH,"%+5.3f", f);
				}
				cnt++;
				w.write(str);
			}
			w.write("\n");
		}
	}

}
