/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.EmptyTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.LinearTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.ScriptingTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;

public class TransformationFactory {
    public static final IFrameTransformationStrategy EMPTY = new EmptyTransformationStrategy();
    public static final IFrameTransformationStrategy LINEAR = new LinearTransformationStrategy();
    public static final IFrameTransformationStrategy SCRIPTING = new ScriptingTransformationStrategy();
    
    private static final List<IFrameTransformationStrategy> strategies;
    
    static {
        strategies = new LinkedList<IFrameTransformationStrategy>();
        strategies.add(EMPTY);
        strategies.add(LINEAR);
        strategies.add(SCRIPTING);
    }
    
    protected TransformationFactory() {
    }

	public static IFrameTransformationStrategy loadXML(Element child) {
		String name = child.getAttributeValue("name");
		
		for (IFrameTransformationStrategy strg : strategies) {
			if(strg.getXMLName().equals(name)) {
				return strg.loadXML(child);
			}
		}
		
		return new EmptyTransformationStrategy();
	}
}
