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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Set the robot in the current frame.
 * 
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionSave extends AbstractAction {

	/** controller to manipulate frames */
	protected MainApplication app;

	@Inject
	ActionSave(IImageLoader loader, MainApplication appController) {
		super("Save to file");
		final String name = "filesave.png";
		putValue(Action.LARGE_ICON_KEY, loader.large(name));
		putValue(Action.SMALL_ICON, loader.small(name));
		app = appController;
		putValue(SHORT_DESCRIPTION, "Save the current animation.");
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

	public void actionPerformed(ActionEvent e) {

		if (app.getCurFile() == null) {
				File file = chooseFile();
				
				if(file == null) {
					return;
				}
				
				if(!file.getName().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}
				
				app.setCurFile(file);
		}

		Element root = new Element("motion-editor").setAttribute("version", "1");
		Document doc = new Document(root);

		app.getStandardStrategy().toXML(root);

		Element frameListElem = new Element("frame-list");
		root.addContent(frameListElem);

		for (NaoFrame frame : app.getFrameList()) {
			frame.toXML(frameListElem);
		}

		try {
			OutputStream ostream = new BufferedOutputStream(
					new FileOutputStream(app.getCurFile()));
			XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
			serializer.output(doc, ostream);
			ostream.flush();
			ostream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
