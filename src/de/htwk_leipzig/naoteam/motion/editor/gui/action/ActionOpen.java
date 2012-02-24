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
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.TransformationFactory;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Set the robot in the current frame.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionOpen extends AbstractAction  {

    /** controller to manipulate frames */
    protected MainApplication app;

    @Inject
    ActionOpen(IImageLoader loader, MainApplication appController) {
        super("Open from file");
        final String name = "fileopen.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        putValue(Action.SHORT_DESCRIPTION, "Open a save animation.");
        app = appController;
    }

    @SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		File file = chooseFile();
		
		if(file == null) {
			return;
		}

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(file);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(doc == null) {
			return;
		}

		Element root = doc.getRootElement();
		
		app.setStandardStrategy(TransformationFactory.loadXML(root.getChild("transformation")));
		
		Element frameListElem = root.getChild("frame-list");
		List<Element> frames = frameListElem.getChildren();
		List<NaoFrame> loadedFrames = new LinkedList<NaoFrame>();
		
		for (Element f : frames) {
			loadedFrames.add(NaoFrame.loadXML(f));
		}
		
		app.setFrameList(loadedFrames);
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

		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MotionEditor files (*.xml)", "xml"));
		int result = chooser.showOpenDialog(app.getMainWindow());

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
}
