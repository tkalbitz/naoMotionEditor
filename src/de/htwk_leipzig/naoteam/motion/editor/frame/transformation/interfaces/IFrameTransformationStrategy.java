/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces;

import java.util.List;

import javax.swing.JPanel;

import org.jdom.Element;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;

/**
 * A transformation merge on frame in an an other. For the transformation
 * information can be supplied. 
 * 
 * @author Tobias Kalbitz
 */
public interface IFrameTransformationStrategy {
    
    /**
     * @return the name of the transformation.
     */
    String getName();
    
    /**
     * Transform the from-frame in the to-frame with the config vars found in config
     * @param from
     * @param to
     * @param config
     * @return
     */
    List<NaoFrame> transform(NaoFrame from, NaoFrame to);
    
    /** 
     * @return the widget which will be displayed to configure the strategy
     */
    JPanel getConfigComponent();

	void toXML(Element e);

	String getXMLName();

	IFrameTransformationStrategy loadXML(Element elem);
}
