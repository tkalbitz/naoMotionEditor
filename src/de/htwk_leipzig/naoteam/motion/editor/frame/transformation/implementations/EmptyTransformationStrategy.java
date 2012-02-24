/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations;

import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.jdom.Element;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;

/**
 * This transformation does nothing.
 * 
 * @author Tobias Kalbitz
 */
public class EmptyTransformationStrategy implements
        IFrameTransformationStrategy {

    public String getName() {
        return "No Transformation";
    }

    public List<NaoFrame> transform(NaoFrame from, NaoFrame to) {
        return Collections.emptyList();
    }

	public JPanel getConfigComponent() {
		return new JPanel();
	}

	public void toXML(Element e) {
		e.addContent(new Element("transformation").setAttribute("name", getXMLName()));
	}

	public String getXMLName() {
		return "empty";
	}

	public IFrameTransformationStrategy loadXML(Element elem) {
		return new EmptyTransformationStrategy();
	}

}
