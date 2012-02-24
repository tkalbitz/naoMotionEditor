/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
public class LinearTransformationStrategy implements
        IFrameTransformationStrategy {
	
	private int frames;
	JPanel configPanel;
	private JTextField framesField;

	public LinearTransformationStrategy() {
		this(150);
	}
	
    public LinearTransformationStrategy(int f) {
		frames = f;
	}

	public String getName() {
        return "Linear";
    }

    public List<NaoFrame> transform(NaoFrame from, NaoFrame to) {
    	List<NaoFrame> list = new LinkedList<NaoFrame>();
    	
    	float [] fromAc = from.getAllActuatorPos();
    	float [] toAc   = to.getAllActuatorPos();
    	
    	for(int curFrame = 1; curFrame < frames; curFrame++) {
    		float [] newAc = new float[NaoConstants.DOF];
    		
	    	for(int joint = 0; joint < NaoConstants.DOF; joint++) {
	    		float delta  = (toAc[joint] - fromAc[joint]) / frames;
	    		newAc[joint] = fromAc[joint] + delta * curFrame; 
	    	}
	    	
	    	list.add(new NaoFrame(newAc));
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
		
		IFrameTransformationConfigBuilder builder = MainApplication.injector.getInstance(IFrameTransformationConfigBuilder.class);
		builder.addInteger(framesField, "frames", "Frames");
		configPanel = builder.createPanel();
		
		return configPanel;
	}

	public void toXML(Element e) {
		e.addContent(new Element("transformation").setAttribute("name", getXMLName()).setAttribute("frame-count", Integer.toString(frames)));
	}

	public String getXMLName() {
		return "linear";
	}

	public IFrameTransformationStrategy loadXML(Element elem) {
		LinearTransformationStrategy s = new LinearTransformationStrategy();
		s.frames = Integer.parseInt(elem.getAttributeValue("frame-count"));
		return s;
	}

}
