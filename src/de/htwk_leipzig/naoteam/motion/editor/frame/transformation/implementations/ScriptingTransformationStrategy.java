/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Element;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationConfigBuilder;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * This transformation does nothing.
 * 
 * @author Tobias Kalbitz
 */
public class ScriptingTransformationStrategy implements
        IFrameTransformationStrategy {
	
	private final class LoadFileActionFilter implements ActionListener {
		private final JTextArea scriptField;
		private final JButton loadFromFile;

		private LoadFileActionFilter(JTextArea scriptField, JButton loadFromFile) {
			this.scriptField = scriptField;
			this.loadFromFile = loadFromFile;
		}

		public void actionPerformed(ActionEvent e) {
			File file = chooseFile();
			
			if(file == null) {
				return;
			}
			
			/* standard script */
			StringBuffer buffer = new StringBuffer();

			try {
				InputStream istream = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
				
				final String lsep = System.getProperty("line.separator");
		
				String text;
				
				while((text = reader.readLine()) != null) {
					buffer.append(text).append(lsep);
				}
				
				reader.close();
			}catch (Exception ex) {
				ex.printStackTrace();
			}		
			
			script = buffer.toString();
			scriptField.setText(script);
		}

		private File chooseFile() {
			final Preferences prefs = Preferences.userNodeForPackage(getClass());
			String searchDir = prefs.get("defaultSearchDir", null);			
			if(searchDir != null) {
				File curDir = new File(searchDir);
				
				if(!curDir.exists() || !curDir.isDirectory()) {
					searchDir = null;
				}
			}
			final JFileChooser chooser = new JFileChooser(searchDir);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Groovy script files (*.groovy)", "groovy"));
			int result = chooser.showOpenDialog(loadFromFile);

			switch (result) {
			case JFileChooser.CANCEL_OPTION:
				return null;
			case JFileChooser.ERROR_OPTION:
				return null;
			}				
			
			File file = chooser.getSelectedFile();
			prefs.put("defaultSearchDir", file.getParent());
			try {
				prefs.flush();
			} catch (BackingStoreException e1) {
				e1.printStackTrace();
			}
			return file;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + frames;
		result = prime * result + ((script == null) ? 0 : script.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScriptingTransformationStrategy other = (ScriptingTransformationStrategy) obj;
		if (frames != other.frames) {
			return false;
		}
		if (script == null) {
			if (other.script != null) {
				return false;
			}
		} else if (!script.equals(other.script)) {
			return false;
		}
		return true;
	}

	private int frames;
	private String script;
	
	JPanel configPanel;
	private JTextField framesField;
 
	private static final String STD_SCRIPT;
	
	static {
		/* standard script */
		StringBuffer buffer = new StringBuffer();

		try {
			InputStream istream = ScriptingTransformationStrategy.class.getResourceAsStream("/example.groovy");
			BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
			
			final String lsep = System.getProperty("line.separator");
	
			String text;
			
			while((text = reader.readLine()) != null) {
				buffer.append(text).append(lsep);
			}
	
		}catch (Exception e) {
			e.printStackTrace();
		}		
		
		STD_SCRIPT = buffer.toString();
	}
	
	
	public ScriptingTransformationStrategy(ScriptingTransformationStrategy strg) {
		script = strg.script;
		frames = strg.frames;
	}
	
	public ScriptingTransformationStrategy() {
		frames = 150;
		configPanel = null;
		script = STD_SCRIPT;
	}
	
    public String getName() {
        return "Scripting";
    }

    public List<NaoFrame> transform(NaoFrame from, NaoFrame to) {
    	List<NaoFrame> list = new LinkedList<NaoFrame>();
    	
    	float [] fromAc = from.getAllActuatorPos();
    	float [] toAc   = to.getAllActuatorPos();
    	float [] newAc  = new float[NaoConstants.DOF];
    	
    	ScriptEngineManager sm = new ScriptEngineManager();
    	ScriptEngine se = sm.getEngineByName("groovy");
    	
    	se.put("frameList", list);
    	se.put("fromFrame", from);
    	se.put("toFrame", to);
    	se.put("fromAc", fromAc);
    	se.put("toAc", toAc);
    	se.put("newAc", newAc);
    	se.put("frameCount", frames);
    	se.put("DOF", NaoConstants.DOF);

    	try {
			se.eval(script);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
    	 
        return list;
    }

	public JPanel getConfigComponent() {
		
		if(configPanel != null) {
			return configPanel;
		}
		
		framesField = new JTextField(String.valueOf(frames));
		framesField.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				insertUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e) {
				try {
					frames = Integer.parseInt(framesField.getText());
				} catch (Exception ex) {
					/* nothing todo here */
				}
			}
			
			public void changedUpdate(DocumentEvent e) {
			}
		});
		
		final JTextArea scriptField = new JTextArea(script, 10, 20);
		scriptField.getDocument().addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(DocumentEvent e) {
				insertUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e) {
				script = scriptField.getText();
			}
			
			public void changedUpdate(DocumentEvent e) {
			}
		});
		
		final JButton loadFromFile = new JButton("Load from file...");
		loadFromFile.addActionListener(new LoadFileActionFilter(scriptField, loadFromFile));
			
		IFrameTransformationConfigBuilder builder = MainApplication.injector.getInstance(IFrameTransformationConfigBuilder.class);
		builder.addInteger(framesField, "frames", "Frames");
		
		JScrollPane scrollPane = new JScrollPane(scriptField);
		builder.addString(scrollPane, "script", "Script");
		builder.addInteger(loadFromFile, "load", "");
		configPanel = builder.createPanel();
		
		return configPanel;
	}

	public void toXML(Element e) {
		e.addContent(new Element("transformation").setAttribute("name", getXMLName()).setAttribute("frame-count", Integer.toString(frames)).setText(script));
	}

	public String getXMLName() {
		return "script";
	}

	public IFrameTransformationStrategy loadXML(Element elem) {
		ScriptingTransformationStrategy s = new ScriptingTransformationStrategy();
		s.frames = Integer.parseInt(elem.getAttributeValue("frame-count"));
		s.script = elem.getText();
		return s;
	}

	public void setFrames(int f) {
		frames = f;
		if(framesField != null) {
			framesField.setText(String.valueOf(f));
		}
	}

}
