/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Public API to build a transformation configuration. It's a standard builder
 * pattern.
 * 
 * @author Tobias Kalbitz
 */
public interface IFrameTransformationConfigBuilder {

	/**
	 * Add a integer with a given name and a caption to the config.
	 * 
	 * @param name
	 *            name of the config element
	 * @param caption
	 *            caption in the UI
	 * @return {@link INumberConstraint}
	 */
	INumberConstraint<Integer> addInteger(JComponent field, String name, String caption);

	/**
	 * Add a double with a given name and a caption to the config.
	 * 
	 * @param name
	 *            name of the config element
	 * @param caption
	 *            caption in the UI
	 * @return {@link INumberConstraint}
	 */
	INumberConstraint<Double> addDouble(JComponent field, String name, String caption);

	/**
	 * Add a float with a given name and a caption to the config.
	 * 
	 * @param name
	 *            name of the config element
	 * @param caption
	 *            caption in the UI
	 * @return {@link INumberConstraint}
	 */
	INumberConstraint<Float> addFloat(JComponent field, String name, String caption);

	/**
	 * Add a separator with a name to the config.
	 * 
	 * @param caption
	 *            the caption
	 * @return {@link IFrameTransformationConfigBuilder}
	 */
	IFrameTransformationConfigBuilder addSeparator(String caption);

	JPanel createPanel();

	void addString(JComponent field, String name, String caption);
}
